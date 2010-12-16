package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 */
public class Navigation {

    @FindBy(id="Navigation")
    private WebElement navigation;

    @Deprecated
    public void checkThatElementsArePresent() {
        navigation.findElement(By.id("Filteroptions"));

        // TODO: iterate sites...
    }
}
