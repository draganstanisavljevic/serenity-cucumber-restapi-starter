package implementations.backend.auth;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.lib.configuration.User;
import com.orgname.qa.lib.configuration.UsersProvider;
import com.orgname.qa.lib.models.auth.TokenPayload;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;

import static com.orgname.qa.lib.configuration.ConfigurationProvider.getAuth0ClientConfiguration;
import static com.orgname.qa.lib.restassured.Request.post;
import static com.orgname.qa.lib.restassured.Request.then;
import static com.orgname.qa.lib.restassured.RestAssuredFactory.getRestClient;

public class TokenApi {
    private static final String TOKEN_PATH = "access_token";
    private static final String BASE_PATH = "oauth/token/";

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
}
