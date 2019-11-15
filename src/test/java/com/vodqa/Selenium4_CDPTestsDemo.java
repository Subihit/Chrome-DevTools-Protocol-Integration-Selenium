package com.vodqa;

import com.vodqa.messaging.MessageBuilder;
import com.vodqa.messaging.Messages;
import com.vodqa.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.network.model.ConnectionType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.openqa.selenium.devtools.network.Network.emulateNetworkConditions;
import static org.openqa.selenium.devtools.network.Network.enable;


public class Selenium4_CDPTestsDemo {

    private static ChromeDriver driver;
    private static DevTools devTools;

    @BeforeClass
    public static void initDriverAndDevTools() {
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, System.getProperty("user.dir") + "/driver/chromedriver");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, System.getProperty("user.dir") + "/target/chromedriver.log");
        driver = new ChromeDriver();
        devTools = driver.getDevTools();
        devTools.createSession();

    }

    @Test
    public void simulateNetworkBandwidth() {
        driver.get("http://www.google.com");
        devTools.createSession();
        devTools.send(enable(Optional.of(100000), Optional.empty(), Optional.empty()));
        devTools.send(emulateNetworkConditions(false, 100, 1000, 2000,
                Optional.of(ConnectionType.cellular4g)));
        driver.get("http://www.google.com");
        new Utils().waitFor(10);

    }

    @Test
    public void mockGeoLocation() {
        driver.get("https://www.google.com/maps");
        MessageBuilder simulateGeoLocation = Messages.overrideLocation("Emulation.setGeolocationOverride",
                40.6892494, -74.0466891);
        driver.executeCdpCommand(simulateGeoLocation.method, simulateGeoLocation.params);
        new Utils().waitFor(5);
        driver.findElement(By.cssSelector(".widget-mylocation-button-icon-common")).click();
        new Utils().waitFor(10);

    }

    @AfterClass
    public static void driverQuit() {
        driver.quit();
    }

}
