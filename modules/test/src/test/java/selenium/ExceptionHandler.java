package selenium;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * @author jogri
 */
public class ExceptionHandler {

    /**
     * The URL pointing to the folder at the Hudson server where the target
     * folder of the project is located.
     */
    private static final String HUDSON_URL = "http://hudson.kantega.lan/job/OpenAksess-trunk-functional-testing/ws/trunk/modules/test/target/";

    /**
     * Creates a folder named {@code selenium} in the project module's
     * {@code target} folder. Here, a screenshot and HTML source code of the
     * current page are saved when an error occurs.
     *
     * @param webDriver The driver associated with the current browser window.
     * @param clazz The current JUnit test class.
     * @throws IOException
     */
    public static void createErrorReport(WebDriver webDriver, Class clazz) throws IOException {
        System.out.println("Test failed at " + webDriver.getCurrentUrl());

        // Create Selenium folder for storage of error report files
        File targetDir = new File("target");
        File seleniumDir = new File(targetDir, "selenium");
        seleniumDir.mkdir();
        System.out.println("seleniumDir created at " + seleniumDir.getAbsolutePath());

        // Save screenshot
        TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
        File screenshot = takesScreenshot.getScreenshotAs(OutputType.FILE);
        File screenshotCopy = new File(seleniumDir, clazz.getSimpleName() + ".png");
        FileUtils.copyFile(screenshot, screenshotCopy);
        screenshot.delete();
        if ("hudson.kantega.lan".equals(InetAddress.getLocalHost().getHostName()) || "backup".equals(InetAddress.getLocalHost().getHostName())) {
            System.out.println("Screenshot: " + HUDSON_URL + seleniumDir + "/" + screenshotCopy.getName());
        } else {
            System.out.println("Screenshot was saved at " + screenshotCopy.getAbsolutePath());
        }
        
        // Save HTML source code
        File pageSource = new File(seleniumDir, clazz.getSimpleName() + ".html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(pageSource));
        bw.write(webDriver.getPageSource());
        bw.close();
        if ("hudson.kantega.lan".equals(InetAddress.getLocalHost().getHostName()) || "backup".equals(InetAddress.getLocalHost().getHostName())) {
            System.out.println("Page source code: " + HUDSON_URL + seleniumDir + "/" + pageSource.getName());
        } else {
            System.out.println("Page source code was saved at " + pageSource.getAbsolutePath());
        }
    }
}
