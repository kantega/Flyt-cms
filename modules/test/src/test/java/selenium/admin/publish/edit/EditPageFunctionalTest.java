package selenium.admin.publish.edit;

import selenium.admin.login.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.admin.pageobjects.EditContentButtons;
import selenium.admin.pageobjects.ModesMenu;
import selenium.admin.pageobjects.SideBar;
import selenium.admin.pageobjects.TabToolsMenu;
import selenium.admin.publish.view.ViewPage;
import static org.junit.Assert.*;

/**
 * @author jogri
 */
public class EditPageFunctionalTest {

    @Test
    public void testClickCancel() throws Exception {
        WebDriver webDriver = new FirefoxDriver();
        webDriver.get("http://prototype.kantega.lan/template-site/admin");

        LoginPage loginPage = PageFactory.initElements(webDriver, LoginPage.class);
        loginPage.login();

        ViewPage viewPage = PageFactory.initElements(webDriver, ViewPage.class);
        viewPage.checkThatPageElementsArePresent("/template-site");
        viewPage.getModesMenu().clickEdit();
        
        EditPage editPage = new EditPage(webDriver);
        editPage.checkThatPageElementsArePresent("/template-site");
        editPage.getEditContentButtons().clickCancel();

        viewPage.checkThatPageElementsArePresent("/template-site");

        webDriver.close();
    }
}
