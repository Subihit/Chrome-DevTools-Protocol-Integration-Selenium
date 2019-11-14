package com.vodqa;

import com.vodqa.utils.Utils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.devtools.Console;
import org.openqa.selenium.devtools.DevTools;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class ChromeDevToolsTest {

    private static ChromeDriver chromeDriver;
    private static DevTools chromeDevTools;

    @BeforeClass
    public static void initDriverAndDevTools() {
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, System.getProperty("user.dir") + "/driver/chromedriver");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, System.getProperty("user.dir") + "/target/chromedriver.log");
        chromeDriver = new ChromeDriver();
        chromeDevTools = chromeDriver.getDevTools();
        chromeDevTools.createSession();

    }

    @Test
    public void verifyConsoleMessageAdded() {

        String consoleMessage = "Hello Selenium 4";

        chromeDevTools.send(Console.enable());

        chromeDevTools.addListener(Console.messageAdded(), consoleMessageFromDevTools ->
                Assert.assertEquals(true, consoleMessageFromDevTools.getText().equals(consoleMessage)));

        chromeDriver.get("https://google.com");

        chromeDriver.executeScript("console.log('" + consoleMessage + "');");
        new Utils().waitFor(10);
    }

    @AfterClass
    public static void driverQuit() {
        chromeDriver.quit();
    }

}
