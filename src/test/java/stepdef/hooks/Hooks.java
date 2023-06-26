package stepdef.hooks;

import com.orgname.qa.lib.properties.PropertiesLoader;
import com.orgname.qa.lib.utils.logger.RestAssuredLogger;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import com.orgname.qa.lib.configuration.ConfigurationProvider;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;

public class Hooks {

    private static boolean oneTimeFlag = false;

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

}
