package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.edit.EditPage;

/**
 * @author jogri
 * @see EditPage
 */
public class SideBar {

    private WebDriver webDriver;

    @FindBy(id="SideBar")
    private RenderedWebElement sideBar;

    public SideBar(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Deprecated
    public void checkThatElementsArePresent() {
        sideBar.findElement(By.className("sidebarFieldset"));
    }

    public boolean isDisplayed() {
        return sideBar.isDisplayed();
    }
}
