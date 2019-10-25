package com.vodqa;

import com.neovisionaries.ws.client.WebSocketException;
import com.vodqa.messaging.CDPClient;
import com.vodqa.messaging.MessageBuilder;
import com.vodqa.utils.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

public class Demo {

    private WebDriver driver;
    Utils utils = new Utils();
    String wsurl;

    @BeforeMethod
    public void setup() throws IOException {
        driver = utils.launchBrowser();
        wsurl = utils.getWebSocketDebuggerURL();
    }

    @AfterMethod
    public void teardown() {
        utils.stopChrome();
    }

    @Test
    public void mockGeoLocation() throws IOException, WebSocketException, InterruptedException {
        CDPClient cdpClient = new CDPClient(wsurl);
        cdpClient.sendMessage(MessageBuilder.geoLocationMessage(90, 51.501364, -0.1440787));
        driver.navigate().to("https://www.google.com.sg/maps");
        Thread.sleep(5000);
        driver.findElement(By.cssSelector(".widget-mylocation-button-icon-common")).click();
        Thread.sleep(10000);
    }


}
