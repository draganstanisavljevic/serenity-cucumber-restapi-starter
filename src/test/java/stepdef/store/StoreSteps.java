package stepdef.store;

import api.store.StoreApi;
import api.store.StorePostApi;
import com.orgname.qa.model.petstore.Order;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import lib.restassured.ResponseAssertion;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Steps;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import static api.store.StorePostApi.CREATE_ORDER_INPUT;
import static lib.assertions.AssertionMessages.isNull;
import static lib.restassured.Request.then;

public class StoreSteps {

    public ValidatableResponse response;
    private final SoftAssertions softAssert = new SoftAssertions();

    @Steps
    ResponseAssertion responseAssertion;

    @Steps
    StoreApi storeApi;

    @Steps
    StorePostApi storePostApi;

    @When("I search for order {word}")
    public void iSearchForOrder(String orderNumber) {
        response = storeApi.getOrder(orderNumber);
    }

    @When("I get inventory")
    public void iGetInventory() {
        response = storeApi.getInventory();
    }

    @Given("There is an order in database")
    @When("I create order")
    public void iCreateStore() {
        response = storePostApi.createOrder();
    }

    @Then("I should see proper details of created order in response")
    public void iShouldSeeProperDetailsOfCreatedOrderInResponse(){
        responseAssertion.assertResponseSuccess();
        Order actualOrder = then().extract()
                .as(Order.class);
        Order expectedOrder = Serenity.sessionVariableCalled(CREATE_ORDER_INPUT);
        Assertions.assertThat(actualOrder).as(isNull("Order")).isNotNull();
        softAssert.assertThat(actualOrder.getId()).as("Order Id").isEqualTo(expectedOrder.getId());
        softAssert.assertThat(actualOrder.getPetId()).as("Pet id").isEqualTo(expectedOrder.getPetId());
        softAssert.assertThat(actualOrder.getQuantity()).as("Quantity").isEqualTo(expectedOrder.getQuantity());
        softAssert.assertThat(actualOrder.getStatus()).as("Status").isEqualTo(expectedOrder.getStatus());
        softAssert.assertThat(actualOrder.getShipDate()).as("Ship date").isEqualTo(expectedOrder.getShipDate());
        softAssert.assertAll();
    }

}
