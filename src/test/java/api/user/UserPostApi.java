package api.user;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.model.User;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.restassured.RestAssuredFactory;

import static lib.restassured.Request.post;

public class UserPostApi {

    private static final String URL_CREATE = "/user";
    public Response response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.PET);

    public Response createUser() {
        User user = new User();
        user.setId(24l);
        user.setUserStatus(23);
        user.setEmail("tft@gy.com");
        user.setFirstName("dragan");
        user.setLastName("stan");

        response = post(requestSpec, URL_CREATE, user);
        return response;
    }
}
