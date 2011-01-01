package selenium.admin.publish.view;

import org.junit.After;
import org.junit.Before;
import selenium.admin.login.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.ExceptionHandler;
import selenium.admin.pageobjects.EditContentButtons;
import selenium.admin.pageobjects.NewSubPage;
import selenium.admin.pageobjects.ToolsMenu;
import static org.junit.Assert.*;

/**
 * @author jogri
 */
public class NewSubPagePageFunctionalTest {

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
    public void testNewSubPage() throws Throwable {
        try {
            LoginPage loginPage = PageFactory.initElements(webDriver, LoginPage.class);
            loginPage.login();

            ToolsMenu toolsMenu = PageFactory.initElements(webDriver, ToolsMenu.class);
            toolsMenu.clickNewSubPage();

            NewSubPage newSubPage = PageFactory.initElements(webDriver, NewSubPage.class);
            newSubPage.selectTemplateId();
            newSubPage.selectAssociationCategory();
            newSubPage.clickContinue();

            assertTrue(newSubPage.isSideBarPresent());
            assertFalse(newSubPage.isErrorMessageAreaVisible());
            EditContentButtons editContentButtons = PageFactory.initElements(webDriver, EditContentButtons.class);
            editContentButtons.clickSave();

            assertTrue(newSubPage.isErrorMessageAreaVisible());
        } catch (Throwable throwable) {
            ExceptionHandler.createErrorReport(webDriver, getClass());
            throw throwable;
        }
    }
}
