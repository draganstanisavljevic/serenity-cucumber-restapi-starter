package helpers;

import com.orgname.qa.lib.webdriver.BasePage;
import org.openqa.selenium.By;

import java.util.Map;

public class HeaderPage extends BasePage {

    public final Map<String, By> locators = Map.ofEntries(
            Map.entry("searchButton", By.cssSelector("input[name=\"searchProducts\"]")),
            Map.entry("searchBox", By.cssSelector("input[name=\"keyword\"]"))
    );
}
