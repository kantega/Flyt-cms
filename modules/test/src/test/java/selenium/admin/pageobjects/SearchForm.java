package selenium.admin.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import selenium.admin.publish.view.ViewPage;

/**
 * @author jogri
 * @see ViewPage
 */
public class SearchForm {

    private WebDriver webDriver;

    @FindBy(id="SearchForm")
    private RenderedWebElement searchForm;

    public SearchForm(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Deprecated
    public void checkThatElementsArePresent() {
        searchForm.findElement(By.id("SearchQuery"));
        searchForm.findElement(By.id("SearchButton"));
    }

    public void performSearch(String query) {
        searchForm.findElement(By.id("SearchQuery")).sendKeys(query);
        searchForm.findElement(By.id("SearchButton")).click();
    }
}
