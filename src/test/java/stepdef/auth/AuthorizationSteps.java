package stepdef.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.orgname.qa.configuration.Services;
import com.orgname.qa.lib.restassured.ResponseAssertion;
import implementations.backend.auth.TokenApi;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Steps;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationSteps {
    private static final String PLACEHOLDER_USER = "placeholderUser";
    private static final String LAST_RESPONSE = "lastResponse";

    @Steps
    TokenApi tokenApi;

    @Steps
    ResponseAssertion responseAssertion;

    @Given("^I (?:have|receive)? a valid token for user '(.*)' in '(.*)' service$")
    public void loginAsTestUser(final String user, final String service) {
        setAuth0Token(user, service);
    }

    @When("^I (?:retrieve|attempt to retrieve)? a valid token for user '(.*)' in '(.*)' service$")
    public void attemptLoginAsTestUser(final String user, final String service) {
        tokenApi.requestToken(user, Services.valueOf(service));
    }

    /**
     * Must be used ONLY when no user is required eg: S2A service
     *
     * @param service Service Name from 'Services' enum
     */
    @Given("I have a valid token for '{word}' service")
    public void validServiceToken(final String service) {
        setAuth0Token(PLACEHOLDER_USER, service);
    }

    private void setAuth0Token(final String user, final String service) {
        Serenity.setSessionVariable("CURRENT_USER").to(user);
        String accessTokenKey = String.format("%s_%s_%s", Services.valueOf(service).getService(), user, "AUTH0");
        if (!Serenity.hasASessionVariableCalled(accessTokenKey)) {
            String authToken = tokenApi.getToken(user, Services.valueOf(service));
            Serenity.setSessionVariable(accessTokenKey).to(authToken);
        }
    }

    @Then("I should receive custom scopes in access token")
    public void iShouldVerifyCustomScopesInAccessToken() {
        ValidatableResponse response = Serenity.sessionVariableCalled(LAST_RESPONSE);
        String accessTokenPayload = response.extract().body().path("access_token").toString();
        DecodedJWT jwt = JWT.decode(accessTokenPayload);
        assertThat(jwt.getClaim("scope").asString()).as("Scope is empty").isNotEmpty();
    }
}
