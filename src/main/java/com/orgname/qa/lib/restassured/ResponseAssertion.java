package com.orgname.qa.lib.restassured;

import io.cucumber.datatable.DataTable;
import io.restassured.path.json.JsonPath;
import net.thucydides.core.annotations.Step;
import org.assertj.core.api.SoftAssertions;
import org.hamcrest.Matchers;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.rest.SerenityRest.then;
import static org.hamcrest.Matchers.*;

public class ResponseAssertion {

    private static final String RESP_ERR_MSG = "Response is not successful!";
    private static final String RESP_BODY_EMPTY_ERR_MSG = "Response body should not be empty!";
    private static final String RESP_BODY_NOT_EMPTY_ERR_MSG = "Response body should be empty!";
    private static final String RESP_MESSAGE_SHOULD_CONTAIN = "Response message does not contain: ";
    private static final String RESP_MESSAGE_SHOULD_EQUAL = "Response message does not equal: ";

    /*private static final SoftAssertions softAssert = Hooks.getSoftAssert();*/

    @Step("Check whether response is successful")
    public void assertResponseSuccess(final String... message) {
        String errMsg = message.length > 0 ? message[0] : RESP_ERR_MSG;
        then().statusCode(describedAs(errMsg, is(HttpStatus.OK.value())));
    }

    @Step("Check whether status code is as {0}")
    public void assertStatusCode(final int statusCode) {
        assertStatusCode(statusCode, String.format("as %s,", statusCode));
    }

    @Step("Check whether status code is as {0} with custom message")
    public void assertStatusCode(final int statusCode, final String message) {
        then().statusCode(describedAs(message, is(statusCode)));
    }

    @Step("Check whether response body is not empty")
    public void assertResponseBodyNotEmpty() {
        then().body("isEmpty()", describedAs(RESP_BODY_EMPTY_ERR_MSG, is(false)));
    }

    @Step("Check whether response body is empty")
    public void assertResponseBodyEmpty() {
        then().body("isEmpty()", describedAs(RESP_BODY_NOT_EMPTY_ERR_MSG, is(true)));
    }

    @Step("Check whether response body has expected data")
    public void validateJson(final DataTable dataTable) {
        JsonPath jsonPath = then().extract().jsonPath();
        List<Map<String, String>> dataToCheckList = dataTable.asMaps();
        SoftAssertions softly = new SoftAssertions();
        dataToCheckList.forEach((Map<String, String> dataToCheck) -> {
            String expectedValue = dataToCheck.get("expectedValue");
            if (expectedValue != null && !expectedValue.isEmpty()) {
                String jsonPathValue = dataToCheck.get("jsonPath");
                softly.assertThat(jsonPath.get(jsonPathValue).toString())
                        .as(jsonPathValue)
                        .isEqualToIgnoringCase(expectedValue);
            }
        });
        softly.assertAll();
    }

/*    @Step("Check whether response body has same data as in file {0}")
    public void validateJson(final String filePath, final String ignoringPaths) {
        String expectedJson = JsonHelper.readJsonFile(filePath);
        String actualJson = then().extract().asString();

        if (!isNullOrEmpty(ignoringPaths)) {
            assertJsonEquals(expectedJson, actualJson, whenIgnoringPaths(ignoringPaths.split(",")));
        } else {
            assertJsonEquals(expectedJson, actualJson);
        }
    }*/

    @Step("Check whether response path {0} contains {1}")
    public void assertResponsePathContains(final String path, final String text) {
        then().body(path, describedAs(RESP_MESSAGE_SHOULD_CONTAIN + text, containsString(text)));
    }

    @Step("Check whether response path {0} equal {1}")
    public void assertResponsePathEqual(final String path, final String text) {
        then().body(path, describedAs(RESP_MESSAGE_SHOULD_EQUAL + text, Matchers.is(text)));
    }

/*    @Step("Validate mandatory fields has expected values. DataTable: {0}")
    public static void validateFieldsHaveExpectedValues(final Map<String, String> dataTable) {
        JsonPath responseJson = then().extract().jsonPath();
        for (Map.Entry<String, String> dataMap : dataTable.entrySet()) {
            String fieldValue = responseJson.getString(dataMap.getKey());
            softAssert.assertThat(fieldValue)
                    .as(String.format("Field %s has no expected value %s", dataMap.getKey(), dataMap.getValue()))
                    .isEqualToIgnoringCase(dataMap.getValue());
        }
        softAssert.assertAll();
    }*/

/*    @Step("Validate mandatory fields exist in the response")
    public static void validateExistingFields(final List<String> dataTable) {
        Map<String, String> responseJson = then().extract().jsonPath().getMap("");

        for (String mandatoryField : dataTable) {
            softAssert.assertThat(responseJson).as(isNotFound(mandatoryField)).containsKey(mandatoryField);
        }
        softAssert.assertAll();
    }*/
}
