package stepdef.frontend;

import com.orgname.qa.lib.assertions.AssertionMessages;
import com.orgname.qa.lib.helpers.StringFormatHelper;
import com.orgname.qa.lib.webdriver.BasePage;
import helpers.PageHelper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Steps;
import org.junit.Assert;
import pages.HomePage;

import java.util.List;

public class PageInteractionSteps extends UIInteractionSteps {

    String errorVisibleMessagePostfix = "visible on Page";

    @Steps
    HomePage homePage;

    @Steps
    BasePage basePage;

    @Steps
    ElementsValidationSteps elementsValidationSteps;

    PageHelper pageHelper;

    @When("I click {string} element on {string} page")
    @When("I click {string} element in the {string}")
    public void iClickElementInMainMenuOnPage(String linkName, String pageName) {
        String linkLocator = StringFormatHelper.toCamelCase(linkName);
        pageHelper.getElementLocator(linkLocator, pageName).click();
    }

    @Then("I should see {string} text as {string} on {string} Page")
    public void iShouldSeeTitleOnPage(String expectedText, String elementName, String pageName) {
        String locator = StringFormatHelper.toCamelCase(elementName);
        basePage.waitFor(pageHelper.getElementLocator(locator, pageName));
        String actualText = pageHelper.getElementLocator(locator, pageName).getText();
        Assert.assertEquals(expectedText, actualText);
    }

    @When("I enter {string} in {string} element in the {string}")
    public void iEnterText(String searchTerm, String linkName, String pageName) {
        String linkLocator = StringFormatHelper.toCamelCase(linkName);
        pageHelper.getElementLocator(linkLocator, pageName).sendKeys(searchTerm);
    }



    @Then("the following elements should{} be present on {string} Page:")
    @Then("the following element should{} be present on {string} Page:")
    @Then("the following element should{} be present on {string} Modal:")
    public void theFollowingElementShouldBePresentOnThePage(String visibility, String pageName, DataTable dataTable) {
        boolean isVisible = !visibility.contains("not");
        List<String> elements = dataTable.asList(String.class);
        for (String elementName : elements) {
            String locator = StringFormatHelper.toCamelCase(elementName);
            String genericErrorMessage = (isVisible) ? AssertionMessages.isNotFound(elementName)
                    : AssertionMessages.isFound(elementName);
            String errorMessage = String.format("'%s' %s", genericErrorMessage, errorVisibleMessagePostfix);
            if (isVisible) {
/*                if ($(homePage.loadingFixed).isVisible()) {
                    $(homePage.loadingFixed).waitUntilNotVisible();
                }*/
                homePage.scrollToElement(pageHelper.getElementLocator(locator, pageName));
                homePage.waitFor(pageHelper.getElementLocator(locator, pageName));
            }
            elementsValidationSteps.iShouldSeeElementOnPage(errorMessage,
                    pageHelper.getElementLocator(locator, pageName), isVisible);
        }
    }
}

