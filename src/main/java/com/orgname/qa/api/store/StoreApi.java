package com.orgname.qa.api.store;

import com.orgname.qa.configuration.Services;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import com.orgname.qa.lib.restassured.RestAssuredFactory;
import net.thucydides.core.annotations.Step;

import static com.orgname.qa.lib.restassured.Request.get;

public class StoreApi {

    private static final String URL_ORDER = "/store/order/%s";
    private static final String URL_INVENTORY = "/store/inventory";

    public ValidatableResponse response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.STORE);

    @Step("Get order {status}")
    public ValidatableResponse getOrder(String orderNumber) {
        response = get(requestSpec, String.format(URL_ORDER, orderNumber));
        return response;
    }

    @Step("Get inventory")
    public ValidatableResponse getInventory() {
        response = get(requestSpec, URL_INVENTORY);
        return response;
    }
}
