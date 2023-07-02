package implementations.backend.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orgname.qa.configuration.Services;
import com.orgname.qa.lib.models.auth.TokenPayload;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;

import java.util.HashMap;

import static com.orgname.qa.lib.restassured.Request.post;
import static com.orgname.qa.lib.restassured.Request.then;
import static com.orgname.qa.lib.restassured.RestAssuredFactory.getRestClient;
import static implementations.backend.auth.TokenApi.generateTokenPayload;

public class SalesforceTokenApi {
    private static final String TOKEN_PATH = "access_token";
    private static final String BASE_PATH = "oauth2/token";

    @Step("Get access token successfully")
    public String getToken(final String user, final Services service) {
        postTokenRequest(user, service);
        return then().statusCode(HttpStatus.SC_OK).extract().body().path(TOKEN_PATH);
    }

    private void postTokenRequest(final String user, final Services service) {
        TokenPayload tokenPayload = generateTokenPayload(user, service);
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<>() {
        };
        HashMap<String, String> tokenQueryParams = new ObjectMapper().convertValue(tokenPayload, typeRef);
        RequestSpecification requestSpecification = getRestClient(service, "auth0Url");
        post(requestSpecification, BASE_PATH, tokenQueryParams);
    }
}
