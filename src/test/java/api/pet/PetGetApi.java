package api.pet;

import com.orgname.qa.configuration.Services;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.restassured.RestAssuredFactory;
import net.thucydides.core.annotations.Step;

import static lib.restassured.Request.get;

public class PetGetApi {

    private static final String URL = "/pet/findByStatus";
    private static final String URL_TAGS = "/pet/findByTags";
    private static final String URL_ID = "/v2/pet/%s";
    public Response response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.PET);

    @Step("Get by {status}")
    public Response getPetByStatus(String status) {
        response = get(requestSpec, URL, "status", status);
        return response;
    }

    @Step("Get by {id}")
    public Response getPetBy(String id) {
        response = get(requestSpec, String.format(URL_ID, id));
        return response;
    }

    @Step("Get by {tag}")
    public Response getByTags(String tag) {
        response = get(requestSpec, URL_TAGS, "tags", tag);
        return response;

    }


}
