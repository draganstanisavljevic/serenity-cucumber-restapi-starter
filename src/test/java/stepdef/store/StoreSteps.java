package stepdef.store;

import api.store.StoreApi;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import net.thucydides.core.annotations.Steps;

public class StoreSteps {

    public Response response;

    @Steps
    StoreApi storeApi;

    @When("I search for order {word}")
    public void iSearchForOrder(String orderNumber) {
        response = storeApi.getOrder(orderNumber);
    }

    @When("I get inventory")
    public void iGetInventory() {
        response = storeApi.getInventory();
    }

    @When("I create order")
    public void iCreateStore() {
        response = storeApi.createOrder();
    }

}
