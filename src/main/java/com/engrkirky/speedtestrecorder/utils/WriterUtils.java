package com.engrkirky.speedtestrecorder.utils;

import com.engrkirky.speedtestrecorder.model.Result;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class WriterUtils {
    private static final String OUTPUT_FILE = "speedtest-results.txt";

    public static void writeResults(List<Result> results) {
        long timestamp = Instant.now().toEpochMilli();
        String filename = String.format("%s-%s", OUTPUT_FILE, timestamp);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Timestamp,ISP,IP,Download_Speed,Upload_Speed\n");

            for (Result result: results) {
                String resultStr = result.getTimestamp() +
                        "," +
                        result.getIsp() +
                        "," +
                        result.getIp() +
                        "," +
                        result.getDl() +
                        "," +
                        result.getUl() +
                        "\n";
                writer.write(resultStr);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
