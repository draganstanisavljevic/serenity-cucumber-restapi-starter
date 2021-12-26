package api.pet;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.model.Category;
import com.orgname.qa.model.Pet;
import com.orgname.qa.model.User;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.restassured.RestAssuredFactory;
import net.thucydides.core.annotations.Step;

import static lib.restassured.Request.get;
import static lib.restassured.Request.post;

public class PetPostApi {

    private static final String URL_CREATE = "/pet";
    public Response response;
    RequestSpecification requestSpec = RestAssuredFactory.getRestClient(Services.PET);

    @Step("Create pet")
    public Response createPet() {
        Pet pet = new Pet();
        pet.setId(24l);
        pet.setName("my name");
        pet.setStatus(Pet.StatusEnum.AVAILABLE);
        Category category = new Category();
        category.setId(21l);
        category.setName("my category");
        pet.setCategory(category);

        response = post(requestSpec, URL_CREATE, pet);
        return response;
    }

    public Response createUser() {
        User user = new User();
        user.setId(24l);
        user.setUserStatus(23);
        user.setEmail("tft@gy.com");
        user.setFirstName("dragan");
        user.setLastName("stan");

        response = post(requestSpec, URL_CREATE, user);
        return response;
    }

}
