package stepdef.frontend;

import com.orgname.qa.lib.configuration.ConfigurationProvider;
import com.orgname.qa.lib.webdriver.MyBaseObject;
import io.cucumber.java.en.When;
import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Steps;

import java.util.Map;

public class FrontendSteps extends UIInteractionSteps {

    @Steps
    MyBaseObject myBaseObject;

    @When("I open {word} page")
    public void openHomePage(String page) {
        Map<String, String> map = ConfigurationProvider.getEnvironmentConfiguration().get("vpg");
        String baseUrl = map.get("webdriver.base.url");
        myBaseObject.openPageUrl(baseUrl, page);
    }

}
