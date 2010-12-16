package selenium.admin.login;

import org.openqa.selenium.WebElement;

/**
 * @author jogri
 */
public class LoginPage {

    private WebElement j_username;

    private WebElement j_password;

    public void login() {
        j_username.sendKeys("Ansattx1");
        j_password.sendKeys("Pils2007!");
        j_password.submit();
    }
}
