package com.vodqa.utils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;

public class Utils {

    private WebDriver driver;

    public WebDriver launchBrowser() {

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.setHeadless(false);

        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.ALL);

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(chromeOptions);
        desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);

        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, System.getProperty("user.dir") + "/driver/chromedriver");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, System.getProperty("user.dir") + "/target/chromedriver.log");

        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder().usingAnyFreePort().withVerbose(true).build();

        try {
            chromeDriverService.start();
        } catch (IOException ioex) {

        }

        driver = new RemoteWebDriver(chromeDriverService.getUrl(), desiredCapabilities);
        return driver;

    }

    public String getWebSocketDebuggerURL() throws IOException {

        String webSocketDebuggerURL = "";
        String jsonEndPointURL = "";
        JSONArray jsonArray = null;

        File file = new File(System.getProperty("user.dir") + "/target/chromedriver.log");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("localhost")) {
                    jsonEndPointURL = line.substring(line.indexOf("http")).replace("/version", "");
                    break;
                }
            }

            URL url = new URL(jsonEndPointURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            jsonArray = new JSONArray(IOUtils.toString(bufferedReader));
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            webSocketDebuggerURL = jsonObject.getString("webSocketDebuggerUrl");

        } catch (IOException ex) {
            throw ex;
        }


        if (webSocketDebuggerURL.equals("") || webSocketDebuggerURL.isEmpty())
            throw new RuntimeException("Could not find webSocketDebuggerURL");

        return webSocketDebuggerURL;

    }


    public int getDynamicID() {
        int min = 100000;
        int max = 999999;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }


    public void stopChrome() {
        driver.close();
        driver.quit();

    }

}
