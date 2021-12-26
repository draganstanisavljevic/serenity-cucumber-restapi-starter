package stepdef.example;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Steps;

import static org.hamcrest.CoreMatchers.equalTo;

public class EmployeeDefinitions {

    private static final String URL = "http://dummy.restapiexample.com/api/v1/employee/";
    public Response response;


    @Given("I send a request to the URL {int} to get user details")
    public void sendRequest(int id) {
        response = SerenityRest.given().contentType("application/json").header("Content-Type", "application/json")
                .when().get(URL + id);
    }

    @Then("the response will return statuscode {int} and id {int} and salary {int} and name {string} and age {int} and message {string}")

    public void verifyValidUser(int statusCode, int id, int salary, String name, int age, String message) {
        SerenityRest.restAssuredThat(response -> response.statusCode(statusCode).and().body("data.id", equalTo(id))
                .and().body("data.employee_salary", equalTo(salary)).and().body("data.employee_name", equalTo(name))
                .and().body("data.employee_age", equalTo(age)).and().body("message", equalTo(message)));

    }

    @Then("the response will return statuscode {int} and status {string} and and message {string}")
    public void verifyInalidUser(int statusCode, String statusMessage, String message) {
 /*       SerenityRest.restAssuredThat(response -> response.statusCode(statusCode).and()
                .body("status", equalTo(statusMessage)).and().body("message", equalTo(message)));*/

    }
}
