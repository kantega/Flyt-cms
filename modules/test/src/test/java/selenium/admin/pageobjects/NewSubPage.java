package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 */
public class NewSubPage {

    // Step 1
    private WebElement templateId;

    private WebElement associationCategory;

    @FindBy(className="ok")
    private WebElement continueButton;

    // Step 2
    @FindBy(id="SideBar")
    private WebElement sideBar;

    @FindBy(id="EditContentPane")
    private WebElement editContentPane;

    public void selectTemplateId() {
        templateId.click();
    }

    public void selectAssociationCategory() {
        associationCategory.click();
    }

    public void clickContinue() {
        continueButton.click();
    }

    public boolean isSideBarPresent() {
        return sideBar.getText() != null;
    }

    public boolean isErrorMessageAreaVisible() {
        RenderedWebElement renderedWebElement = (RenderedWebElement) editContentPane.findElement(By.id("errorMessageArea"));
        return renderedWebElement.isDisplayed();
    }
}
