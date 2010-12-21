package selenium.admin.publish.view;

import selenium.admin.login.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.admin.pageobjects.EditContentButtons;
import selenium.admin.pageobjects.NewSubPage;
import selenium.admin.pageobjects.ToolsMenu;
import static org.junit.Assert.*;

/**
 * @author jogri
 */
public class NewSubPagePageFunctionalTest {

    @Test
    public void testNewSubPage() {
        WebDriver webDriver = new FirefoxDriver();
        webDriver.get("http://prototype.kantega.lan/template-site/admin");

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

        webDriver.close();
    }
}
