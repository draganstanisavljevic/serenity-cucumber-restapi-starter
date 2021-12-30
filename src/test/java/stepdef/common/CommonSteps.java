package stepdef.common;

import io.cucumber.java.en.Then;
import com.orgname.qa.lib.restassured.ResponseAssertion;
import net.thucydides.core.annotations.Steps;
import org.springframework.http.HttpStatus;

public class CommonSteps {

    @Steps
    ResponseAssertion responseAssertion;

    @Then("I should receive status code '{word}'")
    public void assertStatusCode(final String statusCodeText) {
        int expectedStatusCode = HttpStatus.valueOf(statusCodeText).value();
        responseAssertion.assertStatusCode(expectedStatusCode);
    }
}
