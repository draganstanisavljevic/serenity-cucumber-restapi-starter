package stepdef.pet;

import com.orgname.qa.api.pet.PetGetApi;
import com.orgname.qa.api.pet.PetPostApi;
import com.orgname.qa.model.petstore.Category;
import com.orgname.qa.model.petstore.Pet;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.ValidatableResponse;
import com.orgname.qa.lib.restassured.ResponseAssertion;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Steps;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;

import java.util.Objects;

import static com.orgname.qa.api.pet.PetPostApi.*;
import static com.orgname.qa.lib.assertions.AssertionMessages.isNull;
import static com.orgname.qa.lib.restassured.Request.then;

public class PetSteps {

    public static final String PET = "createPetResponse";

    public ValidatableResponse response;
    //TODO why LP create object in Hooks
    private final SoftAssertions softAssert = new SoftAssertions();

    @Steps
    PetGetApi petApi;

    @Steps
    PetPostApi petPostApi;

    @Steps
    ResponseAssertion responseAssertion;

    @When("I retrieve pet by status {word}")
    public void getPetByStatus(String status) {
        response = petApi.getPetByStatus(status);
    }

    @Given("Get pet by id {word}")
    public void getPetById(String id) {
        response = petApi.getPetBy(id);
    }

    @When("I get pet by newly created id")
    public void getPetByNewlyCreatedId() {
        Pet pet = Serenity.sessionVariableCalled(PET);
        response = petApi.getPetBy(String.valueOf(pet.getId()));
    }

    @When("I search by tag {word}")
    public void iSearchByTag(String tag) {
        response = petApi.getByTags(tag);

    }

    @Given("There is a pet in database")
    @When("I create pet")
    public void createPet() {
        response = petPostApi.createPet();
        Pet petResponse = then().extract().as(Pet.class);
        Serenity.setSessionVariable(PET).to(petResponse);
    }

    @Then("I should see proper details of created pet in response")
    public void iShouldSeeProperDetailsOfCreatedPetInResponse(){
        responseAssertion.assertResponseSuccess();
        Pet actualPet = then().extract()
                .as(Pet.class);
        Pet expectedPet = Serenity.sessionVariableCalled(CREATE_PET_INPUT);
        Assertions.assertThat(actualPet).as(isNull("Pet")).isNotNull();
        softAssert.assertThat(actualPet.getId()).as("Pet Id").isEqualTo(expectedPet.getId());
        softAssert.assertThat(actualPet.getName()).as("Name").isEqualTo(expectedPet.getName());
        softAssert.assertThat(actualPet.getStatus()).as("Status").isEqualTo(expectedPet.getStatus());
        softAssert.assertThat(actualPet.getTags().get(0)).as("Tags")
                .isEqualTo(expectedPet.getTags().get(0));
        assertCategory(actualPet.getCategory(), expectedPet.getCategory());
        softAssert.assertAll();
    }

    private void assertCategory(Category actualCategory, Category expectedCategory){
        softAssert.assertThat(Objects.requireNonNull(actualCategory, isNull("Category"))).as("Category Id");
        softAssert.assertThat(actualCategory.getId()).as("Category Id")
                .isEqualTo(expectedCategory.getId());
        softAssert.assertThat(actualCategory.getName()).as("Category name")
                .isEqualTo(expectedCategory.getName());
    }
}
