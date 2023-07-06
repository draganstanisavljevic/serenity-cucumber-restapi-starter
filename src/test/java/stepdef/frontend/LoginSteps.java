package stepdef.frontend;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Steps;
import pages.LoginPage;

public class LoginSteps extends UIInteractionSteps {
    @Steps
    LoginPage loginPage;

    @Given("I am logged in as {word} user")
    @When("I log in as {word} user")
    public void logInPageAsUser(final String user) {
        if (!loginPage.isUserLoggedIn(user)) {
            loginPage.loginAs(user);
        }
    }
}
