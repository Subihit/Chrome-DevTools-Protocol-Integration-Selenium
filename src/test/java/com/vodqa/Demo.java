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
        driver.navigate().to("https://www.google.com.sg/maps");
        utils.waitFor(5);
        driver.findElement(By.cssSelector(".widget-mylocation-button-icon-common")).click();
        utils.waitFor(10);
    }

    @Test
    public void monitorNetworkCalls() throws IOException, WebSocketException, InterruptedException {
        cdpClient.sendMessage(MessageBuilder.enableNetworkCallMonitoringMessage(200));
        driver.navigate().to("http://petstore.swagger.io/v2/swagger.json");
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
        cdpClient.mockResponse("Automate and Chill !");
        driver.navigate().to("http://petstore.swagger.io/v2/swagger.json");
        utils.waitFor(5);
    }


}
