package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author jogri
 * @see EditPage
 */
public class EditContentMain {

    private WebDriver webDriver;

    @FindBy(id="EditContentMain")
    private RenderedWebElement editContentMain;

    public EditContentMain(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
    
    @Deprecated
    public void checkThatElementsArePresent() {
        editContentMain.findElement(By.id("EditContentPane"));
    }
}
