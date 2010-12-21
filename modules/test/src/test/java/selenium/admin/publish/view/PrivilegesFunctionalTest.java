package selenium.admin.publish.view;

import org.apache.commons.lang.StringUtils;
import selenium.admin.login.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.admin.pageobjects.ToolsMenu;
import selenium.admin.pageobjects.UiDialog;
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

        UiDialog uiDialog = PageFactory.initElements(webDriver, UiDialog.class);
        assertTrue(uiDialog.isDisplayed());
        assertFalse(StringUtils.isBlank(uiDialog.getTitle()));
        assertFalse(uiDialog.isIframeEmpty());
        uiDialog.close();

        webDriver.close();
    }
}
