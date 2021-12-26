package stepdef.user;

import api.user.UserApi;
import api.user.UserPostApi;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import net.thucydides.core.annotations.Steps;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserSteps {

    public Response response;

    @Steps
    UserApi userApi;

    @Steps
    UserPostApi userPostApi;

    @When("I search for username {word}")
    public void iSearchForUserName(String userName) {
        response = userApi.getUserByUserName(userName);
    }

    @When("I login with username {word} and password {word}")
    public void iTryToLogin(String userName, String password) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", userName);
        parameters.put("password", password);
        response = userApi.iTryToLogin(parameters);
    }

    @When("I logout")
    public void iLogout() {
        response = userApi.iLogout();
    }

    @Then("I should receive response code {word} user")
    public void iGetResponseStatus(String responseCode) {
        assertThat(response.getStatusCode()).isEqualTo(Integer.valueOf(responseCode));
    }

    @When("I create user")
    public void iCreateUser() {
        response = userPostApi.createUser();
    }
}
