package stepdef.user;

import com.orgname.qa.api.user.UserApi;
import com.orgname.qa.api.user.UserPostApi;
import com.orgname.qa.model.petstore.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import com.orgname.qa.lib.restassured.ResponseAssertion;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Steps;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.HashMap;
import java.util.Map;

import static com.orgname.qa.api.store.StorePostApi.CREATE_ORDER_INPUT;
import static com.orgname.qa.lib.assertions.AssertionMessages.isNull;
import static com.orgname.qa.lib.restassured.Request.then;
import static org.assertj.core.api.Assertions.assertThat;

public class UserSteps {

    public ValidatableResponse response;

    private final SoftAssertions softAssert = new SoftAssertions();

    @Steps
    UserApi userApi;

    @Steps
    ResponseAssertion responseAssertion;

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

    @Given("There is an user in database")
    @When("I create user")
    public void iCreateUser() {
        response = userPostApi.createUser();
    }

    @Then("I should see proper details of created user in response")
    public void iShouldSeeProperDetailsOfCreatedOrderInResponse(){
        responseAssertion.assertResponseSuccess();
        User actualUser = then().extract()
                .as(User.class);
        User expectedUser = Serenity.sessionVariableCalled(CREATE_ORDER_INPUT);
        Assertions.assertThat(actualUser).as(isNull("User")).isNotNull();
        softAssert.assertThat(actualUser.getId()).as("User Id").isEqualTo(expectedUser.getId());
        softAssert.assertThat(actualUser.getFirstName()).as("First name").isEqualTo(expectedUser.getFirstName());
        softAssert.assertThat(actualUser.getLastName()).as("Last name").isEqualTo(expectedUser.getLastName());
        softAssert.assertThat(actualUser.getUserStatus()).as("User status").isEqualTo(expectedUser.getUserStatus());
        softAssert.assertThat(actualUser.getEmail()).as("Email").isEqualTo(expectedUser.getEmail());
        softAssert.assertThat(actualUser.getPassword()).as("Password").isEqualTo(expectedUser.getPassword());
        softAssert.assertThat(actualUser.getPhone()).as("Phone").isEqualTo(expectedUser.getPhone());
        softAssert.assertThat(actualUser.getUsername()).as("Username").isEqualTo(expectedUser.getUsername());
        softAssert.assertAll();
    }
}
