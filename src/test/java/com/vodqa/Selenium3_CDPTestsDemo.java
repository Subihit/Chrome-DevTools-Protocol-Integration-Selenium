package com.vodqa;

import com.neovisionaries.ws.client.WebSocketException;
import com.vodqa.messaging.CDPClient;
import com.vodqa.messaging.Messages;
import com.vodqa.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Selenium3_CDPTestsDemo {

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
    public void mockGeoLocation() throws IOException, WebSocketException {
        cdpClient.sendMessage(Messages.geoLocationMessage(90, "Emulation", "setGeolocationOverride",
                51.501364, -0.1440787));

        driver.navigate().to("https://www.google.com/maps");

        utils.waitFor(5);
        driver.findElement(By.cssSelector(".widget-mylocation-button-icon-common")).click();
        utils.waitFor(10);
    }

    @Test
    public void monitorNetworkCalls() throws IOException, WebSocketException, InterruptedException {
        cdpClient.sendMessage(Messages.enableNetworkCallMonitoringMessage(200, "Network", "enable"));
        driver.navigate().to("http://dummy.restapiexample.com/api/v1/employee/26208");
        utils.waitFor(3);

        JSONObject jsonObject = new JSONObject(cdpClient.getResponseMessage("Network.requestWillBeSent", 5));

        String requestID = jsonObject.getJSONObject("params").getString("requestId");
        cdpClient.sendMessage(Messages.getResponseBodyMessage(2000, requestID));

        String networkResponse = cdpClient.getResponseBodyMessage(2000);
        System.out.println("Network reponse : " + networkResponse);

    }

    @Test
    public void mockResponseCalls() throws Exception {
        cdpClient.sendMessage(Messages.buildRequestInterceptorPatternMessage(2000, "*", "Document"));

        cdpClient.sendMockedResponse("Mock Response !");

        driver.navigate().to("https://twqablore.github.io/vodqa/");
        utils.waitFor(5);
    }


    @Test
    public void mockImageCalls() throws Exception {
        byte[] fileContent = FileUtils.readFileToByteArray(new File(System.getProperty("user.dir") + "/images/cat.png"));

        String encodedFileContent = Base64.getEncoder().encodeToString(fileContent);

        cdpClient.sendMessage(Messages.buildRequestInterceptorPatternMessage(2000, "*", "Image"));

        cdpClient.sendMockedImage(encodedFileContent);

        driver.navigate().to("https://www.amazon.in/");
        utils.waitFor(15);
    }

}
