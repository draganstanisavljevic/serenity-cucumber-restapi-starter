package implementations.backend.auth;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.lib.configuration.User;
import com.orgname.qa.lib.configuration.UsersProvider;
import com.orgname.qa.lib.models.auth.TokenPayload;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static com.orgname.qa.lib.configuration.ConfigurationProvider.getAuth0ClientConfiguration;
import static com.orgname.qa.lib.restassured.Request.post;
import static com.orgname.qa.lib.restassured.Request.then;
import static com.orgname.qa.lib.restassured.RestAssuredFactory.getRestClient;

public class TokenApi {
    private static final String TOKEN_PATH = "access_token";
    private static final String BASE_PATH = "oauth/token/";
    private static final String CPQ_BASE_PATH = "auth/app/v1/token";

    @Step("Get access token successfully")
    public String getToken(final String user, final Services service) {
        requestToken(user, service);
        return then().statusCode(HttpStatus.SC_OK).extract().body().path(TOKEN_PATH);
    }

    @Step("Request token")
    public void requestToken(final String user, final Services service) {
        TokenPayload tokenPayload = generateTokenPayload(user, service);

        RequestSpecification requestSpecification = getRestClient(service, "auth0Url");
        post(requestSpecification, BASE_PATH, tokenPayload);
    }

    @Step("Request urlenc token")
    public void requestUrlencToken(final String user, final Services service) {
        Map<String, String> formParams = generateUrlencTokenPayload(user, service);
        RequestSpecification requestSpecification = getRestClient(service, "auth0Url", ContentType.URLENC);
        requestSpecification.formParams(formParams);
        post(requestSpecification, CPQ_BASE_PATH, formParams);
    }

    @Step("Generate token payload for {0} user")
    public static TokenPayload generateTokenPayload(final String user, final Services service) {
        TokenPayload tokenPayload = getAuth0ClientConfiguration().get(service.getService());

        // Grant_type "client_credentials" doesn't need username and password
        if (!tokenPayload.grantType.equals("client_credentials")) {
            User userCredentials = UsersProvider.getUser(user);
            tokenPayload = tokenPayload.toBuilder()
                    .username(userCredentials.getUserName())
                    .password(userCredentials.getPassword())
                    .build();
        }
        return tokenPayload;
    }

    @Step("Generate urlcenc token payload for {0} user")
    public static Map<String, String> generateUrlencTokenPayload(final String user, final Services service) {
        TokenPayload tokenPayload = getAuth0ClientConfiguration().get(service.getService());
        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", tokenPayload.getClientId());
        formParams.put("client_secret", tokenPayload.getClientSecret());
        formParams.put("grant_type", tokenPayload.getGrantType());
        return formParams;
    }
}
