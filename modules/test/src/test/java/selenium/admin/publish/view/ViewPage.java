package selenium.admin.publish.view;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.admin.pageobjects.MainPane;
import selenium.admin.pageobjects.ModesMenu;
import selenium.admin.pageobjects.Navigation;
import selenium.admin.pageobjects.SearchForm;
import selenium.admin.pageobjects.ToolsMenu;
import selenium.admin.pageobjects.TopMenu;

/**
 * @author jogri
 * @see AdminTestMojo
 */
public class ViewPage {

    private TopMenu topMenu;

    private ModesMenu modesMenu;

    private SearchForm searchForm;

    private ToolsMenu toolsMenu;

    private Navigation navigation;

    private MainPane mainPane;

    public ViewPage(WebDriver webDriver) {
        topMenu = PageFactory.initElements(webDriver, TopMenu.class);
        modesMenu = PageFactory.initElements(webDriver, ModesMenu.class);
        searchForm = PageFactory.initElements(webDriver, SearchForm.class);
        toolsMenu = PageFactory.initElements(webDriver, ToolsMenu.class);
        navigation = PageFactory.initElements(webDriver, Navigation.class);
        mainPane = PageFactory.initElements(webDriver, MainPane.class);
    }

    public void checkThatPageElementsArePresent(String contextPath) {
        topMenu.checkThatElementsArePresent(contextPath);
        modesMenu.checkThatElementsArePresent();
        searchForm.checkThatElementsArePresent();
        toolsMenu.checkThatElementsArePresent();
        navigation.checkThatElementsArePresent();
        mainPane.checkThatElementsArePresent();
    }

    public MainPane getMainPane() {
        return mainPane;
    }

    public ModesMenu getModesMenu() {
        return modesMenu;
    }

    public SearchForm getSearchForm() {
        return searchForm;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public ToolsMenu getToolsMenu() {
        return toolsMenu;
    }

    public TopMenu getTopMenu() {
        return topMenu;
    }
}
