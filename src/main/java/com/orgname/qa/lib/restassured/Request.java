package com.orgname.qa.lib.restassured;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;

import java.util.Map;

public class Request {
    private static final String MULTIPART_CONTENT = "multipart/form-data";
    private static final String TEXT_XML_CONTENT = "text/xml";
    public static final String LAST_RESPONSE = "lastResponse";


    public static ValidatableResponse get(final RequestSpecification requestSpec, final String url){
        ValidatableResponse response = requestSpec
                .when().get(url).then();
        Serenity.setSessionVariable(LAST_RESPONSE).to(response);
        return response;
    }

    public static ValidatableResponse get(final RequestSpecification requestSpec, final String url, final String queryPramName, final String queryParamValue){
        ValidatableResponse response = requestSpec
                .queryParam(queryPramName, queryParamValue)
                .when().get(url).then();
        Serenity.setSessionVariable(LAST_RESPONSE).to(response);
        return response;
    }

    public static ValidatableResponse get(final RequestSpecification requestSpec, final String url, final Map<String, String> parameters){
        ValidatableResponse response = requestSpec
                .queryParams(parameters)
                .when().get(url).then();
        Serenity.setSessionVariable(LAST_RESPONSE).to(response);
        return response;
    }

    public static ValidatableResponse post(final RequestSpecification requestSpec, final String url, Object body){
        ValidatableResponse response = requestSpec
                .body(body)
                .when().post(url).then();
        Serenity.setSessionVariable(LAST_RESPONSE).to(response);
        return response;
    }

    public static ValidatableResponse post(final RequestSpecification rs,
                                           final String url,
                                           final MultiPartSpecification multiPart) {
        ValidatableResponse response = rs.contentType(MULTIPART_CONTENT).multiPart(multiPart).post(url).then();
        Serenity.setSessionVariable(LAST_RESPONSE).to(response);
        return response;
    }

    public static ValidatableResponse postSoap(final RequestSpecification rs, final String url, final Object body) {
        ValidatableResponse response = rs.contentType(TEXT_XML_CONTENT).body(body)
                .post(String.format("%s/converter.asmx", url)).then();
        Serenity.setSessionVariable(LAST_RESPONSE).to(response);
        return response;
    }

    public static ValidatableResponse then() {
        return Serenity.sessionVariableCalled(LAST_RESPONSE);
    }
}
