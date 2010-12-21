package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 */
public class Navigation {

    private WebDriver webDriver;

    @FindBy(id="Navigation")
    private RenderedWebElement navigation;

    public Navigation(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Deprecated
    public void checkThatElementsArePresent() {
        navigation.findElement(By.id("Filteroptions"));

        // TODO: iterate sites...
    }
}
