package selenium.admin.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 */
public class MainPane {

    @FindBy(id="MainPane")
    private WebElement mainPane;

    @Deprecated
    public void checkThatElementsArePresent() {
        
    }
}
