package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 */
public class NewSubPage {

    private WebDriver webDriver;

    // Step 1
    private RenderedWebElement templateId;

    private RenderedWebElement associationCategory;

    @FindBy(className="ok")
    private RenderedWebElement continueButton;

    // Step 2
    @FindBy(id="SideBar")
    private RenderedWebElement sideBar;

    @FindBy(id="EditContentPane")
    private RenderedWebElement editContentPane;

    public NewSubPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

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
