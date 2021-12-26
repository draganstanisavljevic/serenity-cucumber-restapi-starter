package api.store;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.model.Order;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.restassured.RestAssuredFactory;
import net.thucydides.core.annotations.Step;
import org.reflections.Store;

import static lib.restassured.Request.get;
import static lib.restassured.Request.post;

public class StoreApi {

    private static final String URL_ORDER = "/store/order/%s";
    private static final String URL_INVENTORY = "/store/inventory";
    private static final String URL_CREATE_ORDER = "/store/order";

    public Response response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.STORE);

    @Step("Get order {status}")
    public Response getOrder(String orderNumber) {
        response = get(requestSpec, String.format(URL_ORDER, orderNumber));
        return response;
    }

    @Step("Get inventory")
    public Response getInventory() {
        response = get(requestSpec, URL_INVENTORY);
        return response;
    }

    @Step("Create order")
    public Response createOrder() {
        Order order = new Order();
        order.setId(21l);
        order.setComplete(true);
        order.setQuantity(11);
        order.setStatus(Order.StatusEnum.DELIVERED);
        order.setPetId(211l);

        response = post(requestSpec, URL_CREATE_ORDER, order);

        return response;
    }
}
