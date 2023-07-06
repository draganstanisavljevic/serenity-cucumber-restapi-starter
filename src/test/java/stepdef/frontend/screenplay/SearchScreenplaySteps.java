package stepdef.frontend.screenplay;

import com.orgname.qa.lib.helpers.StringFormatHelper;
import helpers.PageHelper;
import io.cucumber.java.en.When;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import org.openqa.selenium.Keys;

public class SearchScreenplaySteps extends UIInteractionSteps {

    PageHelper pageHelper;

    @When("I search for {string} in {string} element in the {string} using screenplay")
    public void iSearchFor(String searchTerm, String linkName, String pageName) {
        WebElementFacade searchBox = pageHelper.getElementLocator(linkName, pageName);
        Actor.named("The user")
                .attemptsTo(Enter.theValue(searchTerm)
                            .into(searchBox).thenHit(Keys.ENTER));
    }

    @When("I click {string} element on {string} page using screenplay")
    @When("I click {string} element in the {string} using screenplay")
    public void iClickOnElement(String linkName, String pageName) {
        String linkLocator = StringFormatHelper.toCamelCase(linkName);
        Actor.named("The user")
                .attemptsTo(Click.on(pageHelper.getElementLocator(linkLocator, pageName)));
    }
}
