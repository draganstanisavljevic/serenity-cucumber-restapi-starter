package com.orgname.qa.lib.webdriver;

import com.orgname.qa.lib.helpers.CommonHelper;
import com.orgname.qa.lib.helpers.StringFormatHelper;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;

public class BasePage extends PageObject {
    protected EnvironmentVariables env;
    CommonHelper commonHelper = new CommonHelper();

    /**
     * This method sets an implicit wait only for the specific click event.
     * That means it overrides the global implicit wait duration with the set one.
     * The method clicks the specified WebElementFacade element after setting the implicit wait duration.
     * After clicking the element, the implicit wait duration is reset to the global value.
     *
     * @param element The WebElementFacade element to click on.
     * @param time    The duration in seconds for the implicit wait to be set before clicking the element.
     */

    @Step("Click '{0}' element with waiting duration of '{1}' seconds")
    public void clickElement(final WebElementFacade element, int time) {
        setImplicitTimeout(time, SECONDS);
        clickOn(element);
        resetImplicitTimeout();
    }

    /**
     * Clicks on the specified element.
     *
     * @param element the element to be clicked
     */
    @Step("Click '{0}' element")
    public void clickElement(final WebElementFacade element) {
        clickOn(element);
    }

    /**
     * Clicks on the specified element with the given polling duration.
     *
     * @param element     the element to be clicked
     * @param pollingTime the polling duration
     */
    @Step("Click '{0}' element with polling duration of {1} seconds")
    public void clickElement(final WebElementFacade element, Duration pollingTime) {
        commonHelper.waitForConditiontWithDelay(String.format("Element %s isn't clickable", element),
                pollingTime,
                ExpectedConditions.elementToBeClickable(element));
        clickOn(element);
    }

    @Step("JS click '{0}' element")
    public void jsClick(WebElementFacade element) {
        JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", $(element));
    }

    @Step("Select '{0}' element")
    public void selectElement(final WebElementFacade element) {
        element.waitUntilPresent();
        element.click();
    }

    @Step("Get '{0}' element text")
    public String getElementText(final WebElementFacade element) {
        element.waitUntilPresent();
        return element.getText();
    }

    /**
     * This method submits the given text to the specified input WebElementFacade.
     * This method is not usable anymore as element.clear() is not properly clearing the input.
     *
     * @param element The WebElementFacade input to submit the text to.
     * @param text    The String text to submit to the WebElementFacade input.
     */
    @Step("Submit '{1}' text to '{0}' input")
    public void submitTextOld(final WebElementFacade element, final String text) {
        element.waitUntilVisible();
        String elementValue = element.getValue();
        if (!elementValue.isEmpty()) {
            element.clear();
        }
        element.sendKeys(text);
    }

    /**
     * This method submits the given text to the specified input WebElementFacade.
     * This method is an updated version of submitTextOld() and uses a workaround to clear the input properly
     * before submitting the new text. It uses the operating system name to determine which key combination to use.
     *
     * @param element The WebElementFacade input to submit the text to.
     * @param text    The String text to submit to the WebElementFacade input.
     */
    @Step("Submit '{1}' text to '{0}' input")
    public void submitText(final WebElementFacade element, final String text) {
        element.waitUntilVisible();
        String elementValue = element.getValue();
        if (!elementValue.isEmpty()) {
            String osName = System.getProperty("os.name");
            if (osName.contains("Mac")) {
                $(element).sendKeys(Keys.COMMAND + "A");
                $(element).sendKeys(Keys.BACK_SPACE);
            } else {
                $(element).sendKeys(Keys.CONTROL + "A");
                $(element).sendKeys(Keys.BACK_SPACE);
            }
        }
        element.sendKeys(text);
    }

    @Step("Move to element {0} and click")
    public void hoverAndClick(final WebElementFacade element) {
        Actions action = new Actions(getDriver());
        action.moveToElement($(element)).click().perform();
    }

    @Step("Submit '{word}' text to '{0}' input")
    public void submitTextThenEnter(final String text, final WebElementFacade element) {
        submitText($(element), String.format("%s%n", text));
    }

    @Step("Click element {0} and wait until element {1} to be present")
    public void clickAndWaitForElementToBePresent(final WebElementFacade elementToClick,
                                                  final WebElementFacade elementToWait) {
        clickElement(elementToClick);
        elementToWait.waitUntilPresent();
    }

    @Step("Click element {0} and wait until element {1} to be clickable")
    public void clickAndWaitForElementToBeClickable(final WebElementFacade elementToClick,
                                                    final WebElementFacade elementToWait) {
        clickElement(elementToClick);
        elementToWait.waitUntilClickable();
    }

    @Step("Click element {0} and wait until element {1} to be visible")
    public void clickAndWaitForElementToBeVisible(final WebElementFacade elementToClick,
                                                  final WebElementFacade elementToWait) {
        clickElement(elementToClick);
        elementToWait.waitUntilVisible();
    }

    /**
     * Iframe methods
     */

    @Step("I switch to iframe with name {0}")
    public void switchToIframeWithName(final String iframeName) {
        getDriver().switchTo().frame(iframeName);
    }

    @Step("I switch to iframe with index {0}")
    public void switchToIframeWithIndex(final Integer iframeIndex) {
        getDriver().switchTo().frame(iframeIndex);
    }

    @Step("I switch to default content")
    public void switchToDefaultContent() {
        getDriver().switchTo().defaultContent();
    }

    /**
     * The below session variable steps are for logging purpose
     */

    @Step("Set '{1}' to '{0}' session variable")
    public void setSessionVariable(final String key, final Object value) {
        Serenity.setSessionVariable(key).to(value);
    }

    @Step("Remove '{0}' from session variable")
    public void removeSessionVariable(final String key) {
        Serenity.getCurrentSession().remove(key);
    }

    @Step("Check whether '{0}' is exists in session variable")
    public boolean checkSessionVariable(final String key) {
        return Serenity.hasASessionVariableCalled(key);
    }

    @Step("Get '{0}' from session variable")
    public <T> T getSessionVariable(final String key) {
        return Serenity.sessionVariableCalled(key);
    }

    /**
     * Open page methods
     */

    @Step("Open url with '{0}'")
    public void openPage(String pageName) {
        openPageUrl(String.format("pages.%s", StringFormatHelper.toCamelCase(pageName)));
    }

    @Step("Open url with '{0}' and '{1}'")
    public void openPage(String pageName, String locale) {
        openPageUrlWithLocale(String.format("pages.%s.%s", StringFormatHelper.toCamelCase(pageName), locale), locale);
    }

    @Step("Open url with '{0}' and '{1}' and '{2}'")
    public void openPage(String scope, String pageName, String locale) {
        String relativeUrl = getRelativeUrl(scope, pageName, locale);
        openPageUrlWithLocale(relativeUrl, locale);
    }

    @Step("Open url with '{0}' and '{1}' and '{2}' and '{3}'")
    public void openPage(String scope, String pageName, String locale, String id) {
        String relativeUrl = getRelativeUrl(scope, pageName, locale);
        openPageUrlWithLocaleAndId(relativeUrl, locale, id);
    }

    public void openPageUrl(final String path) {
        super.openAt(env.getProperty(path));
    }

    public void openPageUrlWithLocale(final String path, final String locale) {
        String relativePath = String.format("/%s%s", locale, env.getProperty(path));
        super.openAt(relativePath);
    }

    public void openPageUrlWithLocaleAndId(final String path, final String locale, final String id) {
        String relativePath = String.format("/%s%s/%s", locale, env.getProperty(path), id);
        super.openAt(relativePath);
    }

    private String getRelativeUrl(String scope, String pageName, String locale) {
        String relativeUrl = String.format("pages.%s.%s.%s", StringFormatHelper.toCamelCase(scope),
                StringFormatHelper.toCamelCase(pageName), locale);
        if (env.getProperty(relativeUrl) == null) {
            relativeUrl = String.format("pages.%s.%s.%s", StringFormatHelper.toCamelCase(scope),
                    StringFormatHelper.toCamelCase(pageName), "default");
        }
        return relativeUrl;
    }

    /**
     * ScrollTo methods
     */

    @Step("Scroll to the bottom of the page")
    public void scrollToTheBottomOfThePage() {
        evaluateJavascript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @Step("Scroll to specific webelement of the page")
    public void scrollToElement(WebElementFacade element) {
        evaluateJavascript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Switch to window methods
     */

    @Step("Switch to child window")
    public void switchWindow() {
        String parent = getDriver().getWindowHandle();
        Set<String> allWindowHandles = getDriver().getWindowHandles();
        Iterator<String> iterator = allWindowHandles.iterator();
        while (iterator.hasNext()) {
            String childWindow = iterator.next();
            if (!parent.equals(childWindow)) {
                getDriver().switchTo().window(childWindow);
            }
        }
    }

    @Step("Switch to default window")
    public void closeWindowAndSwitchWindowToDefault() {
        String child = getDriver().getWindowHandle();
        Set<String> allWindowHandles = getDriver().getWindowHandles();
        Iterator<String> iterator = allWindowHandles.iterator();
        while (iterator.hasNext()) {
            String childWindow = iterator.next();
            if (!child.equals(childWindow)) {
                getDriver().close();
                getDriver().switchTo().window(childWindow);
            }
        }
    }

    @Step("Wait for page being loaded for {int} seconds")
    public void waitForPageBeingLoaded(int timeOutInSeconds) {
        commonHelper.waitForPageLoad(timeOutInSeconds);
    }
}

