package selenium.admin.publish.edit;

import selenium.admin.pageobjects.SideBar;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import selenium.admin.pageobjects.EditContentButtons;
import selenium.admin.pageobjects.EditContentMain;
import selenium.admin.pageobjects.ModesMenu;
import selenium.admin.pageobjects.TabToolsMenu;
import selenium.admin.pageobjects.TopMenu;

/**
 * @author jogri
 * @see AdminTestMojo
 */
public class EditPage {

    private TopMenu topMenu;

    private ModesMenu modesMenu;

    private TabToolsMenu tabToolsMenu;

    private EditContentMain editContentMain;

    private EditContentButtons editContentButtons;

    private SideBar sideBar;

    public EditPage(WebDriver webDriver) {
        topMenu = PageFactory.initElements(webDriver, TopMenu.class);
        modesMenu = PageFactory.initElements(webDriver, ModesMenu.class);
        tabToolsMenu = PageFactory.initElements(webDriver, TabToolsMenu.class);
        editContentMain = PageFactory.initElements(webDriver, EditContentMain.class);
        editContentButtons = PageFactory.initElements(webDriver, EditContentButtons.class);
        sideBar = PageFactory.initElements(webDriver, SideBar.class);
    }

    public void checkThatPageElementsArePresent(String contextPath) {
        topMenu.checkThatElementsArePresent(contextPath);
        modesMenu.checkThatElementsArePresent();
        tabToolsMenu.checkThatElementsArePresent();
        editContentMain.checkThatElementsArePresent();
        editContentButtons.checkThatElementsArePresent();
        sideBar.checkThatElementsArePresent();
    }

    public ModesMenu getModesMenu() {
        return modesMenu;
    }

    public TabToolsMenu getTabToolsMenu() {
        return tabToolsMenu;
    }

    public TopMenu getTopMenu() {
        return topMenu;
    }

    public EditContentButtons getEditContentButtons() {
        return editContentButtons;
    }

    public EditContentMain getEditContentMain() {
        return editContentMain;
    }

    public SideBar getSideBar() {
        return sideBar;
    }
}
