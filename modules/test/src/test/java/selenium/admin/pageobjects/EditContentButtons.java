package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author jogri
 * @see EditPage
 */
public class EditContentButtons {

    @FindBy(id="EditContentButtons")
    private WebElement editContentButtons;

    @Deprecated
    public void checkThatElementsArePresent() {
        editContentButtons.findElement(By.className("save"));
        editContentButtons.findElement(By.className("savedraft"));
        editContentButtons.findElement(By.className("cancel"));
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
