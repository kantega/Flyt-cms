package selenium.admin.publish.view;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import selenium.admin.login.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.ExceptionHandler;
import selenium.admin.pageobjects.ToolsMenu;
import selenium.admin.pageobjects.UiDialog;
import static org.junit.Assert.*;

/**
 * @author jogri
 */
public class PrivilegesFunctionalTest {

    private WebDriver webDriver;

    @Before
    public void setUp() {
        webDriver = new FirefoxDriver();
        webDriver.get("http://prototype.kantega.lan/template-site/admin");
    }

    @After
    public void tearDown() {
        webDriver.close();
    }

    @Test
    public void testClickPrivileges() throws Throwable {
        try {
            LoginPage loginPage = PageFactory.initElements(webDriver, LoginPage.class);
            loginPage.login();

            ToolsMenu toolsMenu = PageFactory.initElements(webDriver, ToolsMenu.class);
            toolsMenu.clickPrivileges();

            UiDialog uiDialog = PageFactory.initElements(webDriver, UiDialog.class);
            assertTrue(uiDialog.isDisplayed());
            assertFalse(StringUtils.isBlank(uiDialog.getTitle()));
            assertFalse(uiDialog.isIframeEmpty());
            uiDialog.close();
        } catch (Throwable throwable) {
            ExceptionHandler.createErrorReport(webDriver, getClass());
            throw throwable;
        }
    }
}
