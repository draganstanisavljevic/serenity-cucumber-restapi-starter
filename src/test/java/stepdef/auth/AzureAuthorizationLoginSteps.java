package stepdef.auth;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.lib.configuration.ConfigurationProvider;
import com.orgname.qa.lib.utils.exceptions.AzureAuthorizationLoginException;
import com.orgname.qa.lib.utils.helpers.Pkce;
import com.orgname.qa.lib.webdriver.AzureAuthorizationPage;
import implementations.backend.auth.AzureAuthorizationLoginApi;
import io.cucumber.java.en.Given;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Steps;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.After;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;

public class AzureAuthorizationLoginSteps extends UIInteractionSteps {
    @Steps
    AzureAuthorizationPage azureAuthorizationPage;

    @Steps
    AzureAuthorizationLoginApi azureAuthorizationLoginApi;

    public static final String SCOPE = "scope";
    public static final String REDIRECT_URL = "redirectUrl";
    private static final int WAIT_TIME_FOR_PAGE_TO_BE_LOADED = 9000;

    @After
    public void closeBrowser() {
        getDriver().quit();
    }

    @Given("I have a valid azure token for user {string} in {string} service")
    public void loginAsAzureTestUser(final String user, final String service) throws AzureAuthorizationLoginException {
        testsConfiguration();
        setAzureADToken(user, service);
    }

    public void setAzureADToken(String user, String service)
            throws AzureAuthorizationLoginException {
        String accessTokenKey = String.format("%s_%s_%s", Services.valueOf(service).getService(), user, "AUTH0");
        if (!Serenity.hasASessionVariableCalled(accessTokenKey)) {
            String authToken = azureAuthorizationLoginApi.getAzureToken(user, Services.valueOf(service),
                    extractCode(service));
            Serenity.setSessionVariable(accessTokenKey).to(authToken);
        }
        Serenity.setSessionVariable("CURRENT_USER").to(user);
    }

    private String extractCode(String service) throws AzureAuthorizationLoginException {
        azureLoginFlow(service);
        // After login oauth will redirect to redirect url with authorization code
        var url = azureAuthorizationPage.getDriver().getCurrentUrl();
        return extractCodeFromUrl(url);
    }

    public void azureLoginFlow(String service) throws AzureAuthorizationLoginException {
        Map<String, String> usersValues = testsConfiguration().get(Services.valueOf(service).getService());
        Serenity.setSessionVariable("CURRENT_USER").to(usersValues);
        azureAuthorizationPage.openPageUrl(getAuthorizationCodeUrl(service));
        waitABit(WAIT_TIME_FOR_PAGE_TO_BE_LOADED);
        azureAuthorizationPage.submitText($(azureAuthorizationPage.usernameText), usersValues.get("userName"));
        azureAuthorizationPage.clickElement($(azureAuthorizationPage.nextButton));
        waitABit(WAIT_TIME_FOR_PAGE_TO_BE_LOADED);
        azureAuthorizationPage.submitText($(azureAuthorizationPage.passwordText), usersValues.get("password"));
        azureAuthorizationPage.clickElement($(azureAuthorizationPage.submitButton));
        waitABit(WAIT_TIME_FOR_PAGE_TO_BE_LOADED);
        azureAuthorizationPage.clickElement($(azureAuthorizationPage.nextButton));
    }

    public static Map<String, Map<String, String>> testsConfiguration() {
        ConfigurationProvider.loadAzureConfiguration();
        Map<String, Map<String, String>> testsConfiguration;
        testsConfiguration = ConfigurationProvider.getEnvironmentConfiguration();
        return testsConfiguration;
    }

    private String extractCodeFromUrl(String url) {
        var queryParamsList = URLEncodedUtils.parse(url, Charset.forName("UTF-8"));
        for (NameValuePair param : queryParamsList) {
            if (param.getName().contains("code")) {
                return param.getValue();
            }
        }
        throw new IllegalArgumentException("Url should contain code as query parameter");
    }

    private String getAuthorizationCodeUrl(String service) throws AzureAuthorizationLoginException {
        Map<String, String> configValues = testsConfiguration().get(Services.valueOf(service).getService());
        URIBuilder authorizationCodeUrl = null;
        try {
            authorizationCodeUrl = new URIBuilder(String.format("%s/%s/%s",
                    "https://login.microsoftonline.com/", configValues.get("tenant"), "/oauth2/v2.0/authorize"));
            authorizationCodeUrl.addParameter("client_id", configValues.get("clientId"));
            authorizationCodeUrl.addParameter(SCOPE, configValues.get(SCOPE));
            authorizationCodeUrl.addParameter("redirect_uri", configValues.get(REDIRECT_URL));
            authorizationCodeUrl.addParameter("state", "123");
            authorizationCodeUrl.addParameter("response_type", "code");
            authorizationCodeUrl.addParameter("code_challenge", Pkce.getCodeVerifierChallenge());
            authorizationCodeUrl.addParameter("code_challenge_method", "S256");
        } catch (URISyntaxException ex) {
            throw new AzureAuthorizationLoginException("Error Message", ex);
        }
        return authorizationCodeUrl.toString();
    }
}
