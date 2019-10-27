package com.vodqa;

import com.neovisionaries.ws.client.WebSocketException;
import com.vodqa.messaging.CDPClient;
import com.vodqa.messaging.MessageBuilder;
import com.vodqa.utils.Utils;
import org.json.JSONObject;
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

    @Test
    public void monitorNetworkCalls() throws IOException, WebSocketException, InterruptedException {
        CDPClient cdpClient = new CDPClient(wsurl);
        cdpClient.sendMessage(MessageBuilder.enableNetworkCallMonitoringMessage(200));
        driver.navigate().to("http://petstore.swagger.io/v2/swagger.json");
        Thread.sleep(3000);
        String responseMessage = cdpClient.getResponseMessage("Network.requestWillBeSent", 5);
        JSONObject jsonObject = new JSONObject(responseMessage);
        jsonObject.getJSONObject("params").getString("requestId");
        String requestID = jsonObject.getJSONObject("params").getString("requestId");
        cdpClient.sendMessage(MessageBuilder.getResponseBodyMessage(2000, requestID));
        String networkResponse = cdpClient.getResponseBodyMessage(2000);
        System.out.println("Network reponse : " + networkResponse);

    }


}
