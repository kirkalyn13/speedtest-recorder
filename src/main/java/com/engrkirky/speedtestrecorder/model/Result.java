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
    private Double downloadSpeedMbps;
    private Double uploadSpeedMbps;
    private Double idleLatencyMs;
    private Double downloadLatencyMs;
    private Double uploadLatencyMs;
}
