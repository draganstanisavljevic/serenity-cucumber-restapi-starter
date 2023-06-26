package pages;

import com.orgname.qa.lib.configuration.User;
import com.orgname.qa.lib.configuration.UsersProvider;
import com.orgname.qa.lib.webdriver.BasePage;
import net.serenitybdd.core.Serenity;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;

import java.util.Map;

public class LoginPage extends BasePage {
    public final Map<String, By> locators = Map.ofEntries(
        Map.entry("usernameInput", By.cssSelector("input[name=username]")),
        Map.entry("passwordInput", By.cssSelector("input[name=password]")),
        Map.entry("submitButton", By.cssSelector("input[name=signon]")),
        Map.entry("errorMessage", By.cssSelector("ul.messages"))
    );


    public void loginAs(final String user) {
        User userCredentials = UsersProvider.getUser(user);

        submitText($(locators.get("usernameInput")), userCredentials.getUserName());
        submitText($(locators.get("passwordInput")), userCredentials.getPassword());
        clickElement($(locators.get("submitButton")));

        $(locators.get("submitButton")).waitUntilNotVisible();
        Assertions.assertThat($(locators.get("errorMessage")).isVisible())
                .as("Error message is visible for success login").isFalse();

        Serenity.setSessionVariable("currentUser").to(user);
    }

    /**
     * Check if user is logged in
     *
     * @param user username
     * @return {boolean} - true is user is already logged in, false if user is not logged in
     */
    public boolean isUserLoggedIn(final String user) {
        return user.equals(Serenity.sessionVariableCalled("currentUser"));
    }
}
