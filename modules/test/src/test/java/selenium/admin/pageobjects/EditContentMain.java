package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author jogri
 * @see EditPage
 */
public class EditContentMain {

    @FindBy(id="EditContentMain")
    private WebElement editContentMain;

    @Deprecated
    public void checkThatElementsArePresent() {
        editContentMain.findElement(By.id("EditContentPane"));
    }
}
