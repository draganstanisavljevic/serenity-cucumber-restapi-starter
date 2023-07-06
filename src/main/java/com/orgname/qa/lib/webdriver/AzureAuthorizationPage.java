package com.orgname.qa.lib.webdriver;

import org.openqa.selenium.By;

public class AzureAuthorizationPage extends BasePage {

    public final By usernameText = By.cssSelector("input[name='loginfmt']");
    public final By passwordText = By.cssSelector("input[name='passwd']");
    public final By nextButton = By.id("idSIButton9");
    public final By submitButton = By.cssSelector("input[value='Sign in']");

    public void openPageUrl(String absoluteUrl) {
        super.openUrl(absoluteUrl);
    }
}
