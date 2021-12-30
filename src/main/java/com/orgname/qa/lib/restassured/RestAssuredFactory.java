package com.orgname.qa.lib.restassured;

import com.orgname.qa.configuration.Services;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.orgname.qa.lib.configuration.ConfigurationProvider;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.rest.SerenityRest;
import org.assertj.core.api.Assertions;

import java.util.Map;

import static com.orgname.qa.lib.helpers.DebuggerHelper.isDebugging;

public class RestAssuredFactory {
    private static final ContentType DEFAULT_CONTENT_TYPE = ContentType.JSON;

    private static RequestSpecification createSerenityRest(final Map<String, String> serviceConfiguration,
                                                           final String urlPropertyName) {

        String serviceUrl = serviceConfiguration.get(urlPropertyName);
        Assertions.assertThat(serviceUrl)
                .as(String.format("Property '%s' is not defined in service configuration", urlPropertyName))
                .isNotNull();

        RequestSpecification requestSpecification = SerenityRest
                .given()
                .header("accept", "application/json")
                .contentType(DEFAULT_CONTENT_TYPE)
                .baseUri(serviceUrl);

        if (isDebugging()) {
            requestSpecification.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        }

        return requestSpecification;

    }

    public static RequestSpecification getRestClient(final Services service) {
        RequestSpecification requestSpecification = getRestClient(service, "baseUrl");
        return requestSpecification;
    }

    public static RequestSpecification getRestClient(final Services service, String urlPropertyName) {

        if (Serenity.hasASessionVariableCalled("requestSpecification")) {
            return Serenity.sessionVariableCalled("requestSpecification");
        } else {
            Map<String, String> serviceConfiguration =
                    ConfigurationProvider.getEnvironmentConfiguration().get(service.getService());

            RequestSpecification requestSpecification = createSerenityRest(serviceConfiguration, urlPropertyName);
            Serenity.setSessionVariable("requestSpecification").to(requestSpecification);

            return requestSpecification;
        }
    }
}
