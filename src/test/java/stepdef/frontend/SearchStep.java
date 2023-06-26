package stepdef.frontend;

import com.orgname.qa.lib.webdriver.BasePage;
import helpers.PageHelper;
import io.cucumber.java.en.When;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Steps;

public class SearchStep extends UIInteractionSteps {

    PageHelper pageHelper;

    @Steps
    BasePage basePage;

    @Steps
    PageInteractionSteps pageInteractionSteps;

    @When("I search for {string} in {string} element in the {string}")
    public void iSearchFor(String searchTerm, String linkName, String pageName) {
        pageInteractionSteps.iEnterText(searchTerm, linkName, pageName);
        WebElementFacade searchBox = pageHelper.getElementLocator("searchButton", pageName);
        basePage.submitText(searchBox, searchTerm);
        System.out.println();
    }
}
