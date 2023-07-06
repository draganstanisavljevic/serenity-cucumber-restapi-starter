package stepdef.auth;

import com.orgname.qa.configuration.Services;
import implementations.backend.auth.SalesforceTokenApi;
import io.cucumber.java.en.Given;
import net.serenitybdd.core.Serenity;

public class SalesforceAuthenticationSteps {

    @Given("I receive a valid token for user '{word}' in '{word}' salesforce cloud")
    @Given("I have a valid token for user '{word}' in '{word}' salesforce cloud")
    public void loginAsTestUser(final String user, final String service) {
        Serenity.setSessionVariable("CURRENT_USER").to(user);
        String accessTokenKey = String.format("%s_%s_%s", Services.valueOf(service).getService(), user, "AUTH0");
        String authToken = new SalesforceTokenApi().getToken(user, Services.valueOf(service));
        Serenity.setSessionVariable(accessTokenKey).to(authToken);
    }
}
