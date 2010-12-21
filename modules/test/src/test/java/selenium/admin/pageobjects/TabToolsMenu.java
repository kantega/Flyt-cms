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
public class TabToolsMenu {

    private WebDriver webDriver;

    @FindBy(id="TabToolsMenu")
    private RenderedWebElement tabTolsMenu;

    public TabToolsMenu(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Deprecated
    public void checkThatElementsArePresent() {
        tabTolsMenu.findElement(By.cssSelector("a .content"));
        tabTolsMenu.findElement(By.cssSelector("a .metadata"));
        tabTolsMenu.findElement(By.cssSelector("a .attachments"));
        tabTolsMenu.findElement(By.cssSelector("a .versions"));
    }

    public void clickContent() {
        tabTolsMenu.findElement(By.className("content")).click();
    }

    public void clickMetadata() {
        tabTolsMenu.findElement(By.className("metadata")).click();
    }

    public void clickAttachments() {
        tabTolsMenu.findElement(By.className("attachments")).click();
    }

    public void clickHistory() {
        tabTolsMenu.findElement(By.className("versions")).click();
    }
}
