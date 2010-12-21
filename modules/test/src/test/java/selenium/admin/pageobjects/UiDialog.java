package selenium.admin.pageobjects;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.PrivilegesFunctionalTest;

/**
 * @author jogri
 * @see PrivilegesFunctionalTest
 */
public class UiDialog {

    private WebDriver webDriver;

    @FindBy(id="ui-dialog-title-externalSite")
    private RenderedWebElement uiDialogTitle;

    @FindBy(id="Popup")
    private RenderedWebElement uiDialogIframe;

    @FindBy(className="ui-dialog-titlebar-close")
    private RenderedWebElement uiDialogCloseLink;

    public UiDialog(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public boolean isDisplayed() {
        return ((RenderedWebElement) webDriver.findElement(By.className("ui-dialog"))).isDisplayed();
    }

    public String getTitle() {
        return uiDialogTitle.getText();
    }

    public boolean isIframeEmpty() {
        try {
            webDriver.switchTo().frame("externalSite");

            uiDialogIframe.findElement(By.cssSelector("input.ok"));
            uiDialogIframe.findElement(By.cssSelector("input.cancel"));
        } catch (ElementNotFoundException e) {
            return true;
        } finally {
            webDriver.switchTo().defaultContent();
        }
        return false;
    }

    public void close() {
        uiDialogCloseLink.click();
    }
}
