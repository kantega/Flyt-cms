package selenium.admin.login;

import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.admin.publish.view.ViewPage;
import static org.junit.Assert.*;

/**
 * @author jogri
 */
public class LoginFunctionalTest {

    @Test
    public void testLogin() throws Exception {
        WebDriver webDriver = new FirefoxDriver();
        webDriver.get("http://prototype.kantega.lan/template-site/admin");

        LoginPage loginPage = PageFactory.initElements(webDriver, LoginPage.class);
        loginPage.login();

        ViewPage viewPage = PageFactory.initElements(webDriver, ViewPage.class);
        viewPage.checkThatPageElementsArePresent("/template-site");

        webDriver.close();
    }
}
