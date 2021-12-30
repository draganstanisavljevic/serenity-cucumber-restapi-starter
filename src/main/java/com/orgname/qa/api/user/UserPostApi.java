package com.orgname.qa.api.user;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.model.petstore.User;
import com.orgname.qa.utils.helper.PetApiDataHelper;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import com.orgname.qa.lib.restassured.RestAssuredFactory;
import net.serenitybdd.core.Serenity;

import static com.orgname.qa.lib.restassured.Request.post;

public class UserPostApi {

    private static final String URL_CREATE = "/user";
    private static final String CREATE_USER_INPUT = "createUserInput";
    public ValidatableResponse response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.PET);

    PetApiDataHelper petApiDataHelper = new PetApiDataHelper();

    public ValidatableResponse createUser() {
        User user = petApiDataHelper.setUser();
        Serenity.setSessionVariable(CREATE_USER_INPUT).to(user);
        response = post(requestSpec, URL_CREATE, user);
        return response;
    }
}
