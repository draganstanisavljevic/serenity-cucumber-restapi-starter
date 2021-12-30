import org.junit.runner.RunWith;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(plugin = { "pretty", "com.orgname.qa.cucumber.plugin.MyDemoPlugin" },
                features = "src/test/resources/features", glue = {"stepdef"})
public class SerenityRunnerTest {
}