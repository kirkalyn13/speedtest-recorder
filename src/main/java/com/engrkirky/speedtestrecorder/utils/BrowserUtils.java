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
}
