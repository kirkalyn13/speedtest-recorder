package com.engrkirky.speedtestrecorder.model;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a recorded internet speed test result.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Result (
    String timestamp,
    String isp,
    String ip,
    String location,
    Double downloadSpeedMbps,
    Double uploadSpeedMbps,
    Double idleLatencyMs,
    Double downloadLatencyMs,
    Double uploadLatencyMs
) {}
