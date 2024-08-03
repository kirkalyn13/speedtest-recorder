package com.engrkirky.speedtestrecorder.pages;

import com.engrkirky.speedtestrecorder.model.Result;
import com.engrkirky.speedtestrecorder.utils.BrowserUtils;
import com.engrkirky.speedtestrecorder.utils.WriterUtils;
import com.engrkirky.speedtestrecorder.utils.XPathUtils;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;

public class SpeedtestPage {
    private static final int NAVIGATE_TIME = 60_000;
    private static final int WAIT_TIME = 5_000;
    
    public static void record() {
        try (Playwright playwright = Playwright.create()) {
            List<Result> results = new ArrayList<>();
            for (int i = 0; i < BrowserUtils.getIterations(); i++) {
                Page page = BrowserUtils.launchBrowser(playwright);

                page.navigate(BrowserUtils.getUrl());
                page.click(XPathUtils.BUTTON_XPATH);
                Thread.sleep(NAVIGATE_TIME);

                Result result = new Result();

                result.setTimestamp(Instant.now().toString());
                result.setIsp(page.locator(XPathUtils.ISP_XPATH).innerText());
                result.setIp(page.locator(XPathUtils.IP_XPATH).innerText());
                result.setDl(page.locator(XPathUtils.DOWNLOAD_XPATH).innerText());
                result.setUl(page.locator(XPathUtils.UPLOAD_XPATH).innerText());

                results.add(result);
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(generateSnapshotFilename())));
                printResults(result);

                page.close();
                Thread.sleep(WAIT_TIME);
            }

            WriterUtils.writeResults(results);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printResults(Result result) {
        System.out.println(result.getTimestamp() +
                "," +
                result.getIsp() +
                "," +
                result.getIp() +
                "," +
                result.getDl() +
                ","+
                result.getUl());
    }

    private static String generateSnapshotFilename() {
        long timestamp = Instant.now().toEpochMilli();
        return String.format("speedtest-%s", timestamp);
    }
}
