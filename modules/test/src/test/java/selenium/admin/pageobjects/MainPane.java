package selenium.admin.pageobjects;

import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 */
public class MainPane {

    private WebDriver webDriver;

    @FindBy(id="MainPane")
    private RenderedWebElement mainPane;

    public MainPane(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Deprecated
    public void checkThatElementsArePresent() {
        
    }
}
