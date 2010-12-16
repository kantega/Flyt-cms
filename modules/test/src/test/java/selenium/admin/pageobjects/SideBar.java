package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.edit.EditPage;

/**
 * @author jogri
 * @see EditPage
 */
public class SideBar {

    @FindBy(id="SideBar")
    private WebElement sideBar;

    @Deprecated
    public void checkThatElementsArePresent() {
        sideBar.findElement(By.className("sidebarFieldset"));
    }
}
