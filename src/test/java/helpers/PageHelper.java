package helpers;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.steps.UIInteractionSteps;
import pages.HomePage;

public class PageHelper extends UIInteractionSteps {

    HomePage homePage;
    HeaderPage headerPage;
    SearchResultPage searchResultPage;

    public WebElementFacade getElementLocator(String locator, String pageName) {
        return switch (pageName) {
            case "Home page" -> $(homePage.locators.get(locator));
            case "Header" -> $(headerPage.locators.get(locator));
            case "Search Result Page" -> $(searchResultPage.locators.get(locator));
            default -> null;
        };
    }
}
