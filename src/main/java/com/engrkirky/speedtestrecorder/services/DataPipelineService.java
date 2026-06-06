package com.engrkirky.speedtestrecorder.services;

import com.engrkirky.speedtestrecorder.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

/**
 * Service class for communicating with the data pipeline API.
 */
public class DataPipelineService {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final Properties properties = new Properties();
    private static final String X_API_KEY_HEADER = "X-API-Key";
    private static final String SKIP_MESSAGE = "Skipping sending of results to producer service.";

    private final String baseUrl;
    private final String apiKey;
    private final boolean pipelineEnabled;

    public DataPipelineService(boolean pipelineEnabled, String baseUrl, String apiKey) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.pipelineEnabled = pipelineEnabled;
    }

    /**
     * Checks whether the data pipeline service is enabled or healthy.
     *
     * @return true if the pipeline is enabled and health status is OK, otherwise false
     */
    public boolean isPipelineEnabled() {
        try {
            if (!pipelineEnabled) {
                System.out.printf("Data Pipeline is disabled. %s%n", SKIP_MESSAGE);
                return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/health"))
                    .header(X_API_KEY_HEADER, apiKey)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.printf("Data Pipeline service failed. %s%n", SKIP_MESSAGE);
                return false;
            }

            var json = objectMapper.readTree(response.body());
            return "ok".equalsIgnoreCase(json.path("status").asText());

        } catch (Exception e) {
            System.out.printf("Error encountered in checking the data pipeline. %s%n", SKIP_MESSAGE);
            return false;
        }
    }

    /**
     * Sends a speed test result to the data pipeline service.
     *
     * @param result speed test result payload to send
     */
    public void sendResult(Result result) {
        try {
            String body = objectMapper.writeValueAsString(result);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/speed-test"))
                    .header("Content-Type", "application/json")
                    .header(X_API_KEY_HEADER, apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200) {
                response.statusCode();
            }

        } catch (Exception e) {
            System.out.printf("Error encountered in sending results to pipeline: %s%n", SKIP_MESSAGE);
        }
    }
}
