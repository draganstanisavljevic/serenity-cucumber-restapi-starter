package com.orgname.qa.api.user;

import com.orgname.qa.configuration.Services;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import com.orgname.qa.lib.restassured.RestAssuredFactory;
import net.thucydides.core.annotations.Step;

import java.util.Map;

import static com.orgname.qa.lib.restassured.Request.get;

public class UserApi {

    private static final String URL_BY_USERNAME = "/user/%s";
    private static final String URL_LOGIN = "/user/login";
    private static final String URL_LOGOUT = "/user/logout";
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.USER);

    public ValidatableResponse response;

    @Step("Get user by username {status}")
    public ValidatableResponse getUserByUserName(String username) {
        response =  get(requestSpec, String.format(URL_BY_USERNAME, username));
        return response;
    }

    @Step("I try to login with {parameters}")
    public ValidatableResponse iTryToLogin(Map<String, String> parameters) {
        response =  get(requestSpec, URL_LOGIN, parameters);
        return response;

    }

    @Step("I logout")
    public ValidatableResponse iLogout() {
        response = get(requestSpec, URL_LOGOUT);
        return response;
    }
}
