package api.user;

import com.orgname.qa.configuration.Services;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.restassured.RestAssuredFactory;
import net.thucydides.core.annotations.Step;

import java.util.Map;

import static lib.restassured.Request.get;

public class UserApi {

    private static final String URL_BY_USERNAME = "/user/%s";
    private static final String URL_LOGIN = "/user/login";
    private static final String URL_LOGOUT = "/user/logout";
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.USER);

    public Response response;

    @Step("Get user by username {status}")
    public Response getUserByUserName(String username) {
        response =  get(requestSpec, String.format(URL_BY_USERNAME, username));
        return response;
    }

    @Step("I try to login with {parameters}")
    public Response iTryToLogin(Map<String, String> parameters) {
        response =  get(requestSpec, URL_LOGIN, parameters);
        return response;

    }

    @Step("I logout")
    public Response iLogout() {
        response = get(requestSpec, URL_LOGOUT);
        return response;
    }
}
