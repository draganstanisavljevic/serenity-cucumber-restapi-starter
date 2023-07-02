package implementations.backend.auth;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.lib.restassured.Request;
import com.orgname.qa.lib.restassured.RestAssuredFactory;
import com.orgname.qa.lib.utils.exceptions.AzureAuthorizationLoginException;
import com.orgname.qa.lib.utils.helpers.Pkce;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import java.util.HashMap;
import java.util.Map;

import static stepdef.auth.AzureAuthorizationLoginSteps.*;

public class AzureAuthorizationLoginApi {

    @Step("Get access token successfully")
    public String getAzureToken(String user, Services service, String code) throws AzureAuthorizationLoginException {
        Map<String, String> configValues = testsConfiguration().get(service.getService());

        RequestSpecification requestSpecification = RestAssuredFactory.getRestClient(service,
                "azureUrl");
        configValues.put("code", code);
        configValues.put("codeVerifier", Pkce.getCodeVerifier());
        post(requestSpecification, "oauth2/v2.0/token", configValues);
        return Request.then().statusCode(Integer.parseInt(
                configValues.get("statusCode"))).extract().body().path("access_token");
    }

    public static ValidatableResponse post(RequestSpecification rs, String url, Map<String, String> configValues) {
        Map<String, String> formParams = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Origin", configValues.get(REDIRECT_URL));
        headers.put("Referer", configValues.get(REDIRECT_URL));
        headers.put("content-type", "application/x-www-form-urlencoded;charset=utf-8");
        formParams.put("client_id", configValues.get("clientId"));
        formParams.put("redirect_uri", configValues.get(REDIRECT_URL));
        formParams.put(SCOPE, configValues.get(SCOPE));
        formParams.put("code", configValues.get("code"));
        formParams.put("code_verifier", configValues.get("codeVerifier"));
        formParams.put("grant_type", configValues.get("grantType"));
        formParams.put("client_info", configValues.get("clientInfo"));
        formParams.put("client-request-id", configValues.get("clientRequestId"));

        ValidatableResponse response = rs.headers(headers).formParams(formParams)
                .post(url).then();
        Serenity.setSessionVariable("lastResponse").to(response);
        return response;
    }
}
