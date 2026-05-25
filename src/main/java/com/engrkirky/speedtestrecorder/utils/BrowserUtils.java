package com.engrkirky.speedtestrecorder.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for browser setup and configuration handling.
 */
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

    /**
     * Launches a Chromium browser instance and creates a new page.
     *
     * @param playwright Playwright instance used to launch the browser
     * @return newly created browser page
     */
    public static Page launchBrowser(Playwright playwright) {
        BrowserType browserType = playwright.chromium();
        Browser browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(false));

        return browser.newPage();
    }

    /**
     * Retrieves the configured Speedtest URL.
     *
     * @return configured URL or default URL if not set
     */
    public static String getUrl() {
        return properties.getProperty("url", DEFAULT_URL);
    }

    /**
     * Retrieves the configured number of test iterations.
     *
     * @return configured iteration count or default value if not set
     *
     * @throws NumberFormatException if the iteration value is invalid
     */
    public static int getIterations() {
        return Integer.parseInt(properties.getProperty("iterations", String.valueOf(DEFAULT_ITERATIONS)));
    }

}
