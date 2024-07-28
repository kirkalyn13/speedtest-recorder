package com.engrkirky.speedtestrecorder.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BrowserUtils {
    private static final String PROPERTIES_FILE = "config.properties";
    private static final String DEFAULT_URL = "https://www.speedtest.net/";
    private static final int DEFAULT_ITERATIONS = 1;
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = BrowserUtils.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            }

            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static Page launchBrowser(Playwright playwright) {
        BrowserType browserType = playwright.chromium();
        Browser browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(false));

        return browser.newPage();
    }
    public static String getUrl() {
        return properties.getProperty("url", DEFAULT_URL);
    }

    public static int getIterations() {
        return Integer.parseInt(properties.getProperty("iterations", String.valueOf(DEFAULT_ITERATIONS)));
    }

}
