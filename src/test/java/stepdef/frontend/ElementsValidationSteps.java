package stepdef.frontend;

import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.steps.UIInteractionSteps;
import net.thucydides.core.annotations.Step;
import org.junit.Assert;

public class ElementsValidationSteps extends UIInteractionSteps {

    @Step
    public void iShouldSeeElementOnPage(String errorMessage, WebElementFacade element, boolean isVisible) {
        if (isVisible) {
            Assert.assertTrue(errorMessage, element.isDisplayed());
        } else {
            Assert.assertFalse(errorMessage, element.isVisible());
        }
    }

    @Step
    public void iShouldClickElementOnPage(String errorMessage, WebElementFacade element, boolean isClickable) {
        if (isClickable) {
            Assert.assertTrue(errorMessage, element.isClickable());
        } else {
            Assert.assertFalse(errorMessage, element.isDisabled());
        }
    }
}
