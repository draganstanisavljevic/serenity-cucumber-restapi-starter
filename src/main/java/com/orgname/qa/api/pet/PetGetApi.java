package com.orgname.qa.api.pet;

import com.orgname.qa.configuration.Services;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import com.orgname.qa.lib.restassured.RestAssuredFactory;
import net.thucydides.core.annotations.Step;


import static com.orgname.qa.lib.restassured.Request.get;

public class PetGetApi {

    private static final String URL = "/pet/findByStatus";
    private static final String URL_TAGS = "/pet/findByTags";
    private static final String URL_ID = "/pet/%s";
    public ValidatableResponse response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.PET);

    @Step("Get by {status}")
    public ValidatableResponse getPetByStatus(String status) {
        response = get(requestSpec, URL, "status", status);
        return response;
    }

    @Step("Get by {id}")
    public ValidatableResponse getPetBy(String id) {
        response = get(requestSpec, String.format(URL_ID, id));
        return response;
    }

    @Step("Get by {tag}")
    public ValidatableResponse getByTags(String tag) {
        response = get(requestSpec, URL_TAGS, "tags", tag);
        return response;

    }

}
