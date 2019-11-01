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

public class CDPDemo {

    private WebDriver driver;
    Utils utils = new Utils();
    String wsurl;
    CDPClient cdpClient;

    @BeforeMethod
    public void setup() throws IOException {
        driver = utils.launchBrowser();
        wsurl = utils.getWebSocketDebuggerURL();
        cdpClient = new CDPClient(wsurl);
    }

    @AfterMethod
    public void teardown() {
        utils.stopChrome();
    }

    @Test
    public void mockGeoLocation() throws IOException, WebSocketException, InterruptedException {
        cdpClient.sendMessage(MessageBuilder.geoLocationMessage(90, 51.501364, -0.1440787));

        driver.navigate().to("https://www.google.com/maps");
        utils.waitFor(5);

        driver.findElement(By.cssSelector(".widget-mylocation-button-icon-common")).click();
        utils.waitFor(10);
    }

    @Test
    public void monitorNetworkCalls() throws IOException, WebSocketException, InterruptedException {
        cdpClient.sendMessage(MessageBuilder.enableNetworkCallMonitoringMessage(200));

        driver.navigate().to("http://dummy.restapiexample.com/api/v1/employee/57377");
        utils.waitFor(3);

        String responseMessage = cdpClient.getResponseMessage("Network.requestWillBeSent", 5);
        JSONObject jsonObject = new JSONObject(responseMessage);
        jsonObject.getJSONObject("params").getString("requestId");
        String requestID = jsonObject.getJSONObject("params").getString("requestId");

        cdpClient.sendMessage(MessageBuilder.getResponseBodyMessage(2000, requestID));

        String networkResponse = cdpClient.getResponseBodyMessage(2000);
        System.out.println("Network reponse : " + networkResponse);

    }

    @Test
    public void mockResponseCalls() throws Exception {
        cdpClient.sendMessage(MessageBuilder.buildRequestInterceptorPatternMessage(2000, "*", "Document"));
        cdpClient.sendMockedResponse("Mock Data !");

        driver.navigate().to("http://www.google.com");
        utils.waitFor(5);

        cdpClient.sendMessage(MessageBuilder.buildRequestInterceptorPatternMessage(2000, "*", "Document"));
        cdpClient.sendMockedResponse("Automate and Chill !");

        driver.navigate().refresh();
        utils.waitFor(5);
    }


}
