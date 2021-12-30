package com.orgname.qa.api.store;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.model.petstore.Order;
import com.orgname.qa.utils.helper.PetApiDataHelper;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import com.orgname.qa.lib.restassured.RestAssuredFactory;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import static com.orgname.qa.lib.restassured.Request.post;

public class StorePostApi {

    private static final String URL_CREATE_ORDER = "/store/order";
    public static final String CREATE_ORDER_INPUT = "createUserInput";
    public ValidatableResponse response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.PET);

    PetApiDataHelper petApiDataHelper = new PetApiDataHelper();
    @Step("Create order")
    public ValidatableResponse createOrder() {
        Order order = petApiDataHelper.setOrder();
        Serenity.setSessionVariable(CREATE_ORDER_INPUT).to(order);
        response = post(requestSpec, URL_CREATE_ORDER, order);
        return response;
    }
}
