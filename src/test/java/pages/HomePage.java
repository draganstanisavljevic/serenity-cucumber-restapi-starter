package pages;

import com.orgname.qa.lib.webdriver.BasePage;
import org.openqa.selenium.By;

import java.util.Map;

public class HomePage  extends BasePage {
    public final Map<String, By> locators = Map.ofEntries(
            Map.entry("signIn", By.linkText("Sign In")),
            Map.entry("myBatisLink", By.linkText("www.mybatis.org")),
            Map.entry("banner", By.id("Banner")),
            Map.entry("title", By.linkText("Sign In"))
    );
}
