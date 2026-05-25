package com.engrkirky.speedtestrecorder.model;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a recorded internet speed test result.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class Result {
    private String timestamp;
    private String isp;
    private String ip;
    private String location;
    private String downloadSpeedMbps;
    private String uploadSpeedMbps;
    private String idleLatencyMs;
    private String downloadLatencyMs;
    private String uploadLatencyMs;
}
