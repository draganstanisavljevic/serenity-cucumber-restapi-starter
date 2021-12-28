package api.pet;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.model.petstore.Pet;
import com.orgname.qa.utils.helper.PetApiDataHelper;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lib.restassured.RestAssuredFactory;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import static lib.restassured.Request.post;

public class PetPostApi {

    private static final String URL_CREATE = "/pet";
    public static final String CREATE_PET_INPUT = "createPetInput";
    public ValidatableResponse response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.PET);

    PetApiDataHelper petApiDataHelper = new PetApiDataHelper();

    @Step("Create pet")
    public ValidatableResponse createPet() {
        Pet pet = petApiDataHelper.setPet();
        Serenity.setSessionVariable(CREATE_PET_INPUT).to(pet);
        response = post(requestSpec, URL_CREATE, pet);
        return response;
    }

}
