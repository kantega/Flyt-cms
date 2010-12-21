package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.edit.EditPage;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 * @see EditPage
 */
public class ModesMenu {

    private WebDriver webDriver;

    @FindBy(id="ModesMenu")
    private RenderedWebElement modesMenu;

    public ModesMenu(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Deprecated
    public void checkThatElementsArePresent() {
        modesMenu.findElement(By.cssSelector("a .view"));
        modesMenu.findElement(By.cssSelector("a .edit"));
        modesMenu.findElement(By.cssSelector("a .organize"));
        modesMenu.findElement(By.cssSelector("a .linkcheck"));
        modesMenu.findElement(By.cssSelector("a .statistics"));
        modesMenu.findElement(By.cssSelector("a .notes"));
    }

    public void clickView() {
        modesMenu.findElement(By.cssSelector("a .view")).click();
    }

    public void clickEdit() {
        modesMenu.findElement(By.cssSelector("a .edit")).click();
    }

    public void clickOrganize() {
        modesMenu.findElement(By.cssSelector("a .organize")).click();
    }

    public void clickLinkcheck() {
        modesMenu.findElement(By.cssSelector("a .linkcheck")).click();
    }

    public void clickStatistics() {
        modesMenu.findElement(By.cssSelector("a .statistics")).click();
    }

    public void clickNotes() {
        modesMenu.findElement(By.cssSelector("a .notes")).click();
    }
}
