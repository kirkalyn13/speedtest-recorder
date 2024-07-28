package com.engrkirky.speedtestrecorder.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
    private String timestamp;
    private String isp;
    private String ip;
    private String dl;
    private String ul;
}
