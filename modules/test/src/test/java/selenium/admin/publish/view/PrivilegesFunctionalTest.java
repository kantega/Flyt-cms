package selenium.admin.publish.view;

import org.apache.commons.lang.StringUtils;
import selenium.admin.login.*;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.admin.pageobjects.ToolsMenu;
import static org.junit.Assert.*;

/**
 * @author jogri
 */
public class PrivilegesFunctionalTest {

    @Test
    public void testClickPrivileges() {
        WebDriver webDriver = new FirefoxDriver();
        webDriver.get("http://prototype.kantega.lan/template-site/admin");

        LoginPage loginPage = PageFactory.initElements(webDriver, LoginPage.class);
        loginPage.login();

        ToolsMenu toolsMenu = PageFactory.initElements(webDriver, ToolsMenu.class);
        toolsMenu.clickPrivileges();

        RenderedWebElement privilegesDialog = (RenderedWebElement) webDriver.findElement(By.className("ui-dialog"));
        assertTrue(privilegesDialog.isDisplayed());

        // Verify that the title of the dialog is not empty
        String title = privilegesDialog.findElement(By.id("ui-dialog-title-externalSite")).getText();
        assertFalse(StringUtils.isBlank(title));

        // Verify that the iframe within the dialog box is not empty
        webDriver.switchTo().frame("externalSite");
        WebElement iframe = webDriver.findElement(By.id("Popup"));
        iframe.findElement(By.cssSelector("input.ok"));
        iframe.findElement(By.cssSelector("input.cancel"));

        webDriver.switchTo().defaultContent();

        // Close the dialog
        privilegesDialog.findElement(By.className("ui-dialog-titlebar-close")).click();

        webDriver.close();
    }
}
