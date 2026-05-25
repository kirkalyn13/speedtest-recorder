package com.engrkirky.speedtestrecorder.pages;

import com.engrkirky.speedtestrecorder.model.Result;
import com.engrkirky.speedtestrecorder.services.DataPipelineService;
import com.engrkirky.speedtestrecorder.utils.BrowserUtils;
import com.engrkirky.speedtestrecorder.utils.WriterUtils;
import com.engrkirky.speedtestrecorder.utils.XPathUtils;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;

/**
 * Handles speed test execution and result recording.
 */
public class SpeedtestPage {
    private static final int NAVIGATE_TIME = 60_000;
    private static final int WAIT_TIME = 5_000;

    /**
     * Executes speed tests and stores the collected results.
     *
     * @throws RuntimeException if the thread execution is interrupted
     */
    public static void record(DataPipelineService dataPipelineService) {
        try (Playwright playwright = Playwright.create()) {
            System.out.println("Starting SpeedTest Recording...");
            boolean isPipelineEnabled = dataPipelineService.isPipelineEnabled();
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
                result.setLocation(page.locator(XPathUtils.LOCATION_XPATH).innerText());
                result.setDownloadSpeedMbps(page.locator(XPathUtils.DOWNLOAD_XPATH).innerText());
                result.setUploadSpeedMbps(page.locator(XPathUtils.UPLOAD_XPATH).innerText());
                result.setIdleLatencyMs(page.locator(XPathUtils.IDLE_LATENCY_XPATH).innerText());
                result.setDownloadLatencyMs(page.locator(XPathUtils.DOWNLOAD_LATENCY_XPATH).innerText());
                result.setUploadLatencyMs(page.locator(XPathUtils.UPLOAD_LATENCY_XPATH).innerText());

                closeTrySpeedtest(page);
                results.add(result);
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(generateSnapshotFilename())));
                printResults(result);

                page.close();
                Thread.sleep(WAIT_TIME);
            }

            WriterUtils.writeResults(results);
            if (isPipelineEnabled) dataPipelineService.sendResults(results);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the "Try Speedtest" popup if visible.
     *
     * @param page active browser page instance
     */
    private static void closeTrySpeedtest(Page page) {
        if(page.locator(XPathUtils.TRY_SPEEDTEST_XPATH).isVisible()) {
            page.click(XPathUtils.TRY_SPEEDTEST_XPATH);
        }
    }

    /**
     * Prints the speed test result to the console.
     *
     * @param result recorded speed test result
     */
    private static void printResults(Result result) {
        System.out.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n", 
                result.getTimestamp(),
                result.getIsp(),
                result.getIp(),
                result.getLocation(),
                result.getDownloadSpeedMbps(),
                result.getUploadSpeedMbps(),
                result.getIdleLatencyMs(),
                result.getDownloadLatencyMs(),
                result.getUploadLatencyMs());
    }

    /**
     * Generates a unique screenshot filename.
     *
     * @return generated screenshot filename
     */
    private static String generateSnapshotFilename() {
        long timestamp = Instant.now().toEpochMilli();
        return String.format("speedtest-%s.png", timestamp);
    }
}
