package selenium.admin.login;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author jogri
 */
public class LoginPage {

    private WebDriver webDriver;

    private WebElement j_username;

    private WebElement j_password;

    public LoginPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void login() {
        j_username.sendKeys("Ansattx1");
        j_password.sendKeys("Pils2007!");
        j_password.submit();
    }
}
