package selenium.admin.publish.edit;

import org.junit.After;
import org.junit.Before;
import selenium.admin.login.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.ExceptionHandler;
import selenium.admin.pageobjects.EditContentButtons;
import selenium.admin.pageobjects.ModesMenu;
import selenium.admin.pageobjects.SideBar;
import selenium.admin.pageobjects.TabToolsMenu;
import static org.junit.Assert.*;

/**
 * @author jogri
 */
public class TabToolsFunctionalTest {

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
    public void testClickTabTools() throws Throwable {
        try {
            LoginPage loginPage = PageFactory.initElements(webDriver, LoginPage.class);
            loginPage.login();

            ModesMenu modesMenu = PageFactory.initElements(webDriver, ModesMenu.class);
            modesMenu.clickEdit();

            TabToolsMenu tabToolsMenu = PageFactory.initElements(webDriver, TabToolsMenu.class);
            tabToolsMenu.clickHistory();
            EditContentButtons editContentButtons = PageFactory.initElements(webDriver, EditContentButtons.class);
            SideBar sideBar = PageFactory.initElements(webDriver, SideBar.class);
            assertTrue(editContentButtons.isDisplayed());
            assertTrue(sideBar.isDisplayed());
            tabToolsMenu.clickAttachments();
            assertTrue(editContentButtons.isDisplayed());
            assertTrue(sideBar.isDisplayed());
            tabToolsMenu.clickMetadata();
            assertTrue(editContentButtons.isDisplayed());
            assertTrue(sideBar.isDisplayed());
            tabToolsMenu.clickContent();
            assertTrue(editContentButtons.isDisplayed());
            assertTrue(sideBar.isDisplayed());
            assertFalse(true); // testing of error report...
        } catch (Throwable throwable) {
            ExceptionHandler.createErrorReport(webDriver, getClass());
            throw throwable;
        }
    }
}
