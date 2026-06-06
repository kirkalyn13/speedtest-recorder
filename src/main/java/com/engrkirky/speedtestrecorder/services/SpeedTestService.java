package com.engrkirky.speedtestrecorder.services;

import com.engrkirky.speedtestrecorder.pages.SpeedTestPage;
import com.engrkirky.speedtestrecorder.utils.BrowserUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SpeedTestService {
    private static final String PROPERTIES_FILE = "config.properties";
    private static final String DEFAULT_URL = "https://www.speedtest.net/";
    private static final String PIPELINE_URL = "http://localhost:8081/api";
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
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void run(String[] args) {
        for (String arg : args) {
            String url = arg.startsWith("--url") ? arg.split("=")[1] : getUrl();
            int iterations = arg.startsWith("--iterations=") ? Integer.parseInt(arg.split("=")[1]) : getIterations();
            boolean pipelineEnabled = arg.startsWith("--pipeline-enabled=") ? Boolean.parseBoolean(arg.split("=")[1])  : isPipelineEnabled();
            String pipelineUrl = arg.startsWith("--pipeline-url=") ? arg.split("=")[1] : getPipelineUrl();
            String apiKey = arg.startsWith("--api-key=") ? arg.split("=")[1] : getApiKey();

            System.out.printf("""
                ================================
                Speedtest Recorder
                ================================
                URL:              %s
                Iterations:       %d
                Pipeline Enabled: %s
                Pipeline URL:     %s
                API Key:          %s
                ================================
                %n""", url, iterations, pipelineEnabled, pipelineUrl, apiKey.isBlank() ? "not set" : "***");

            DataPipelineService dataPipelineService = new DataPipelineService(pipelineEnabled, pipelineUrl, apiKey);
            SpeedTestPage.record(dataPipelineService, url, iterations);
        }
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

    /**
     * Returns the configured pipeline URL.
     *
     * @return pipeline URL
     */
    public static String getPipelineUrl() {
        return properties.getProperty("pipeline_url", PIPELINE_URL);
    }

    /**
     * Returns whether pipeline integration is enabled.
     *
     * @return true if enabled, otherwise false
     */
    public static boolean isPipelineEnabled() {
        return Boolean.parseBoolean(
                properties.getProperty("pipeline_enabled", "true"));
    }

    /**
     * Returns the configured API key.
     *
     * @return API key
     */
    public static String getApiKey() {
        return properties.getProperty("api_key", PIPELINE_URL);
    }
}
