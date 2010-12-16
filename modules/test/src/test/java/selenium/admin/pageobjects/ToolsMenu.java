package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 */
public class ToolsMenu {

    @FindBy(id="ToolsMenu")
    private WebElement toolsMenu;

    @Deprecated
    public void checkThatElementsArePresent() {
        toolsMenu.findElement(By.className("newSubpage"));
        toolsMenu.findElement(By.id("DeletePageButton"));
        toolsMenu.findElement(By.id("CutButton"));
        toolsMenu.findElement(By.id("CopyButton"));
        toolsMenu.findElement(By.id("PasteButton"));
        toolsMenu.findElement(By.id("DisplayPeriodButton"));
        toolsMenu.findElement(By.className("privileges"));
    }

    public void clickNewSubPage() {
        toolsMenu.findElement(By.className("newSubpage")).click();
    }

    public void clickPrivileges() {
        toolsMenu.findElement(By.className("privileges")).click();
    }
}
