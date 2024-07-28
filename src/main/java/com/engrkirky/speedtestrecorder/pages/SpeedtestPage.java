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

public class SpeedtestPage {
    public static void record() {
        try (Playwright playwright = Playwright.create()) {
            List<Result> results = new ArrayList<>();
            for (int i = 0; i < BrowserUtils.getIterations(); i++) {
                Page page = BrowserUtils.launchBrowser(playwright);

                page.navigate(BrowserUtils.getUrl());
                page.click(XPathUtils.BUTTON_XPATH);
                Thread.sleep(60_000);

                Result result = new Result();

                result.setTimestamp(Instant.now().toString());
                result.setIsp(page.locator(XPathUtils.ISP_XPATH).innerText());
                result.setIp(page.locator(XPathUtils.IP_XPATH).innerText());
                result.setDl(page.locator(XPathUtils.DOWNLOAD_XPATH).innerText());
                result.setUl(page.locator(XPathUtils.UPLOAD_XPATH).innerText());

                results.add(result);
                printResults(result);

                page.close();
                Thread.sleep(5_000);
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
}
