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
public class SpeedTestPage {
    private static final int NAVIGATE_TIME = 60_000;
    private static final int WAIT_TIME = 5_000;

    /**
     * Executes speed tests and stores the collected results.
     *
     * @throws RuntimeException if the thread execution is interrupted
     */
    public static void record(DataPipelineService dataPipelineService, String url, int iterations) {
        try (Playwright playwright = Playwright.create()) {
            System.out.println("Starting SpeedTest Recording...");
            boolean isPipelineEnabled = dataPipelineService.isPipelineEnabled();
            List<Result> results = new ArrayList<>();

            for (int i = 0; i < iterations; i++) {
                Page page = BrowserUtils.launchBrowser(playwright);

                page.navigate(url);
                page.click(XPathUtils.BUTTON_XPATH);
                Thread.sleep(NAVIGATE_TIME);

                Result result = new Result(
                        Instant.now().toString(),
                        page.locator(XPathUtils.ISP_XPATH).innerText(),
                        page.locator(XPathUtils.IP_XPATH).innerText(),
                        page.locator(XPathUtils.LOCATION_XPATH).innerText(),
                        Double.parseDouble(page.locator(XPathUtils.DOWNLOAD_XPATH).innerText()),
                        Double.parseDouble(page.locator(XPathUtils.UPLOAD_XPATH).innerText()),
                        Double.parseDouble(page.locator(XPathUtils.IDLE_LATENCY_XPATH).innerText()),
                        Double.parseDouble(page.locator(XPathUtils.DOWNLOAD_LATENCY_XPATH).innerText()),
                        Double.parseDouble(page.locator(XPathUtils.UPLOAD_LATENCY_XPATH).innerText()));

                closeTrySpeedtest(page);
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(generateSnapshotFilename())));

                if (isPipelineEnabled) dataPipelineService.sendResult(result);
                printResults(result);

                page.close();
                Thread.sleep(WAIT_TIME);
                results.add(result);
            }

            WriterUtils.writeResults(results);
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
                result.timestamp(),
                result.isp(),
                result.ip(),
                result.location(),
                result.downloadSpeedMbps(),
                result.uploadSpeedMbps(),
                result.idleLatencyMs(),
                result.downloadLatencyMs(),
                result.uploadLatencyMs());
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
