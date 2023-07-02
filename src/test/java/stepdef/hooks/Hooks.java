package stepdef.hooks;

import com.orgname.qa.lib.properties.PropertiesLoader;
import com.orgname.qa.lib.utils.logger.RestAssuredLogger;
import io.cucumber.java.*;
import com.orgname.qa.lib.configuration.ConfigurationProvider;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.thucydides.core.util.EnvironmentVariables;
import org.assertj.core.api.SoftAssertions;

import java.util.Properties;

public class Hooks {

    private static boolean oneTimeFlag = false;
    private static String currentFeatureFile;
    private static SoftAssertions softAssert;
    private EnvironmentVariables environmentVariables;


    @Before
    public static void before(final Scenario scenario) {
        if (!oneTimeFlag) {
            PropertiesLoader.loadSystemProperties();
            PropertiesLoader.loadEnvProperties();

            RestAssuredLogger restAssuredLogger = new RestAssuredLogger();
            RestAssured.config().logConfig(new LogConfig(restAssuredLogger.getPrintStream(), true));

            ConfigurationProvider.loadConfiguration();

            oneTimeFlag = true;
        }
    }

    @Before
    public static void beforeFeature(final Scenario scenario) {
        String[] featureFileUri = scenario.getUri().toString().split("/");
        String featureFileName = featureFileUri[featureFileUri.length - 1];
        if (currentFeatureFile == null) {
            currentFeatureFile = featureFileName;
        } else {
            if (!currentFeatureFile.equals(featureFileName)) {
                Serenity.clearCurrentSession();
                Serenity.getDriver().close();
                currentFeatureFile = featureFileName;
            }
        }
    }

    @BeforeStep
    public static void beforeStep(final Scenario scenario) {
        softAssert = new SoftAssertions();
    }

    @AfterStep
    public static void afterStep(final Scenario scenario) {
        if (scenario.getStatus() == Status.PASSED) {
            softAssert.assertAll();
        }
    }

    @After
    public void attachArtifactsOnFailure(final Scenario scenario) {
        if (scenario.getStatus() == Status.FAILED) {
            String sessionId = Serenity.getCurrentSessionID();
            String moonArtifactsURL = "http://moon-artifacts.tools.leaseplan.io";

            Properties webserviceEndpoint = EnvironmentSpecificConfiguration.from(environmentVariables)
                    .getPropertiesWithPrefix("webdriver.remote.url");

            if (webserviceEndpoint.size() >= 1 && sessionId != null) {
                Serenity.recordReportData().withTitle("Test execution video")
                        .andContents(String.format("%s/%s/video.mp4", moonArtifactsURL, sessionId));
                Serenity.recordReportData().withTitle("Moon session logs")
                        .andContents(String.format("%s/%s/session.log", moonArtifactsURL, sessionId));
            }
        }
    }

    public static SoftAssertions getSoftAssert() {
        return softAssert;
    }

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
    }
}
