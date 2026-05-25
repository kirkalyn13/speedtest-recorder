package com.engrkirky.speedtestrecorder.services;

import com.engrkirky.speedtestrecorder.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

/**
 * Service class for communicating with the data pipeline API.
 */
public class DataPipelineService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final Properties properties = new Properties();
    private static String BASE_URL = properties.getProperty("pipeline_url");


    public DataPipelineService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    static {
        try (var stream = DataPipelineService.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            properties.load(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
        BASE_URL = properties.getProperty("pipeline_url");
    }

    /**
     * Checks whether the data pipeline service is healthy.
     *
     * @return true if the pipeline health status is OK, otherwise false
     */
    public boolean isPipelineHealthy() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/health"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Data Pipeline service failed, skipping sending of results to producer service.");
                return false;
            }

            var json = objectMapper.readTree(response.body());
            return "ok".equalsIgnoreCase(json.path("status").asText());

        } catch (Exception e) {
            System.out.println("Error encountered in checking the data pipeline, skipping sending of results to producer service.");
            return false;
        }
    }

    /**
     * Sends multiple speed test results to the data pipeline service.
     *
     * @param results list of speed test results to send
     */
    public void sendResults(List<Result> results) {
        for (Result result : results) {
            boolean sent = sendResult(result);
            if (!sent) System.out.printf("Failed to send result: %s%n", result.toString());
        }
    }

    /**
     * Sends a speed test result to the data pipeline service.
     *
     * @param result speed test result payload to send
     * @return true if the request was successfully processed, otherwise false
     */
    private boolean sendResult(Result result) {
        try {
            String body = objectMapper.writeValueAsString(result);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/v1/speed-test"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() >= 200 && response.statusCode() < 300;

        } catch (Exception e) {
            return false;
        }
    }
}
