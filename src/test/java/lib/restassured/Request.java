package lib.restassured;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class Request {
    private static final String MULTIPART_CONTENT = "multipart/form-data";
    private static final String TEXT_XML_CONTENT = "text/xml";

    public static Response response;

    public static Response get(final RequestSpecification requestSpec, final String url){
        response = requestSpec
                .when().get(url);
        response.then();
        return response;
    }

    public static Response get(final RequestSpecification requestSpec, final String url, final String queryPramName, final String queryParamValue){
        response = requestSpec
                .queryParam(queryPramName, queryParamValue)
                .when().get(url);
        response.then();
        return response;
    }

    public static Response get(final RequestSpecification requestSpec, final String url, final Map<String, String> parameters){
        response = requestSpec
                .queryParams(parameters)
                .when().get(url);
        response.then();
        return response;
    }

    public static Response post(final RequestSpecification requestSpec, final String url, Object body){
        response = requestSpec
                .body(body)
                .when().post(url);
        response.then();
        return response;
    }
}
