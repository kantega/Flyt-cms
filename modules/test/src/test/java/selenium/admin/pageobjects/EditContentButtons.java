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
public class EditContentButtons {

    private WebDriver webDriver;

    @FindBy(id="EditContentButtons")
    private RenderedWebElement editContentButtons;

    public EditContentButtons(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Deprecated
    public void checkThatElementsArePresent() {
        editContentButtons.findElement(By.className("save"));
        editContentButtons.findElement(By.className("savedraft"));
        editContentButtons.findElement(By.className("cancel"));
    }

    public boolean isDisplayed() {
        return editContentButtons.isDisplayed();
    }

    public void clickSave() {
        editContentButtons.findElement(By.className("save")).click();
    }

    public void clickSaveDraft() {
        editContentButtons.findElement(By.className("savedraft")).click();
    }

    public void clickCancel() {
        editContentButtons.findElement(By.className("cancel")).click();
    }
}
