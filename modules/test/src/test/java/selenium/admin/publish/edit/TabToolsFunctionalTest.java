package selenium.admin.publish.edit;

import selenium.admin.login.*;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.admin.pageobjects.TabToolsMenu;
import selenium.admin.publish.view.ViewPage;
import static org.junit.Assert.*;

/**
 * @author jogri
 */
public class TabToolsFunctionalTest {

    @Test
    public void testClickTabs() throws Exception {
        WebDriver webDriver = new FirefoxDriver();
        webDriver.get("http://prototype.kantega.lan/template-site/admin");

        LoginPage loginPage = PageFactory.initElements(webDriver, LoginPage.class);
        loginPage.login();

        ViewPage viewPage = PageFactory.initElements(webDriver, ViewPage.class);
        viewPage.getModesMenu().clickEdit();
        
        EditPage editPage = new EditPage(webDriver);
        editPage.getTabToolsMenu().clickContent();
        editPage.getTabToolsMenu().clickMetadata();
        editPage.getTabToolsMenu().clickAttachments();
        editPage.getTabToolsMenu().clickHistory();

        webDriver.close();
    }
}
