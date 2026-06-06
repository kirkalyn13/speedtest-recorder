package com.engrkirky.speedtestrecorder.service;

import com.engrkirky.speedtestrecorder.services.DataPipelineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataPipelineServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private DataPipelineService service;

    @BeforeEach
    void setUp() throws Exception {
        service = new DataPipelineService(true, "http://localhost:8081/api", "key");
        var field = DataPipelineService.class.getDeclaredField("httpClient");
        field.setAccessible(true);
        field.set(service, httpClient);
    }

    @Test
    void isPipelineEnabled_returnsFalse_whenDisabled() {
        service = new DataPipelineService(false, "http://localhost:8081/api", "key");
        assertFalse(service.isPipelineEnabled());
    }

    @Test
    void isPipelineEnabled_returnsTrue_whenHealthCheckOk() throws Exception {
        doReturn(200).when(mockResponse).statusCode();
        doReturn("{\"status\":\"ok\"}").when(mockResponse).body();
        doReturn(mockResponse).when(httpClient).send(any(), any());
        assertTrue(service.isPipelineEnabled());
    }

    @Test
    void isPipelineEnabled_returnsFalse_whenHealthCheckFails() throws Exception {
        doReturn(500).when(mockResponse).statusCode();
        doReturn(mockResponse).when(httpClient).send(any(), any());
        assertFalse(service.isPipelineEnabled());
    }

    @Test
    void isPipelineEnabled_returnsFalse_whenExceptionThrown() throws Exception {
        when(httpClient.send(any(), any())).thenThrow(new RuntimeException("connection refused"));
        assertFalse(service.isPipelineEnabled());
    }
}