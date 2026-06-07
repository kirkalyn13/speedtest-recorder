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
        String url = DEFAULT_URL;
        int iterations = DEFAULT_ITERATIONS;
        boolean pipelineEnabled = isPipelineEnabled();
        String pipelineUrl = getPipelineUrl();
        String apiKey = getApiKey();

        for (String arg : args) {
            if (arg.startsWith("--help") || arg.startsWith("--h")) {
                printHelp();
                return;
            }
            else if (arg.startsWith("--url=")) url = arg.split("=")[1];
            else if (arg.startsWith("--iterations=")) iterations = Integer.parseInt(arg.split("=")[1]);
            else if (arg.startsWith("--pipeline-enabled=")) pipelineEnabled = Boolean.parseBoolean(arg.split("=")[1]);
            else if (arg.startsWith("--pipeline-url=")) pipelineUrl = arg.split("=")[1];
            else if (arg.startsWith("--api-key=")) apiKey = arg.split("=")[1];
        }

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

    /**
     * Prints CLI usage and available options for the Speedtest Recorder application.
     */
    private static void printHelp() {
        System.out.println("""
        ================================
        Speedtest Recorder - Help
        ================================
        Usage: java -jar speedtest-recorder.jar [OPTIONS]

        Options:
          --url=<value>               Speedtest URL to run against
                                      Default: https://www.speedtest.net/

          --iterations=<value>        Number of test iterations to run
                                      Default: 1

          --pipeline-enabled=<value>  Enable or disable pipeline publishing
                                      Default: true

          --pipeline-url=<value>      Base URL of the pipeline service
                                      Default: http://localhost:8081/api

          --api-key=<value>           API key for the pipeline service
                                      ⚠ Highly discouraged — configure via config.properties instead

        Example:
          java -jar speedtest-recorder.jar --iterations=3 --pipeline-enabled=true
        ================================
        """);
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
