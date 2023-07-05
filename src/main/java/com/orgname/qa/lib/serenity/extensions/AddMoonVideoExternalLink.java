package com.orgname.qa.lib.serenity.extensions;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.ExternalLink;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public class AddMoonVideoExternalLink implements AfterAWebdriverScenario {

    private static final String MOON_ARTIFACTS_URL = "http://moon-artifacts.tools.example.io";

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        String webdriverRemoteUrl = environmentVariables.getProperty(ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL);
        if (webdriverRemoteUrl != null && webdriverRemoteUrl.contains("moon")) {
            String sessionId = Serenity.getCurrentSessionID();
            testOutcome.setLink(
                    new ExternalLink(
                            String.format("%s/%s/video.mp4", MOON_ARTIFACTS_URL, sessionId), "Moon session video"
                    )
            );
        }
    }
}
