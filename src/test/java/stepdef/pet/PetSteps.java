package stepdef.pet;

import api.pet.PetGetApi;
import api.pet.PetPostApi;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import net.thucydides.core.annotations.Steps;

public class PetSteps {

    public Response response;

    @Steps
    PetGetApi petApi;

    @Steps
    PetPostApi petPostApi;

    @When("I retrieve pet by status {word}")
    public void getPetByStatus(String status) {
        response = petApi.getPetByStatus(status);

    }

    @Given("Get pet by id {word}")
    public void getPetById(String id) {
        response = petApi.getPetBy(id);
    }

    @When("I search by tag {word}")
    public void iSearchByTag(String tag) {
        response = petApi.getByTags(tag);

    }

    @When("I create pet")
    public void createPet() {
        response = petPostApi.createPet();
    }
}
