package pages;

import com.orgname.qa.lib.webdriver.BasePage;
import org.openqa.selenium.By;

import java.util.Map;

public class SearchResultPage extends BasePage {

    public final Map<String, By> locators = Map.ofEntries(
            Map.entry("errorMessage", By.xpath("//div[@id=\"Content\"]/ul/li"))
    );
}
