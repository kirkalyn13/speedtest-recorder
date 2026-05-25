package com.engrkirky.speedtestrecorder;


import com.engrkirky.speedtestrecorder.pages.SpeedtestPage;
import com.engrkirky.speedtestrecorder.services.DataPipelineService;

public class Main {
    public static void main(String[] args) {
        DataPipelineService dataPipelineService = new DataPipelineService();
        SpeedtestPage.record(dataPipelineService);
    }
}