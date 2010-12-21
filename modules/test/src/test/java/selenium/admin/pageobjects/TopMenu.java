package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.edit.EditPage;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 * @see EditPage
 */
public class TopMenu {

    private WebDriver webDriver;

    @FindBy(id="TopMenu")
    private RenderedWebElement topMenu;

    public TopMenu(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Deprecated
    public void checkThatElementsArePresent(String contextPath) {
        topMenu.findElement(By.cssSelector("a[href='" + contextPath + "/admin/mypage/ViewMyPage.action']"));
        topMenu.findElement(By.cssSelector("a[href='" + contextPath + "/admin/publish/Navigate.action']"));
        topMenu.findElement(By.cssSelector("a[href='" + contextPath + "/admin/publish/Navigate.action']"));
        topMenu.findElement(By.cssSelector("a[href='" + contextPath + "/admin/multimedia/Navigate.action']"));
        topMenu.findElement(By.cssSelector("a[href='" + contextPath + "/admin/topicmaps/Topics.action']"));
//        topMenu.findElement(By.cssSelector("a[href='" + contextPath + "/admin/administration/ViewSystemInformation.action']"));
        topMenu.findElement(By.cssSelector("a[href='" + contextPath + "/Logout.action']"));
    }
}
