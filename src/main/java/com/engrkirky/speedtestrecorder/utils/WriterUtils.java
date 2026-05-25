package com.engrkirky.speedtestrecorder.utils;

import com.engrkirky.speedtestrecorder.model.Result;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * Utility class for writing speed test results to a file.
 */
public class WriterUtils {
    private static final String OUTPUT_FILENAME = "speedtest-results";

    /**
     * Writes speed test results to a CSV file.
     *
     * @param results list of recorded speed test results
     *
     * @throws RuntimeException if an I/O error occurs while writing the file
     */
    public static void writeResults(List<Result> results) {
        long timestamp = Instant.now().toEpochMilli();
        String filename = String.format("%s-%s.csv", OUTPUT_FILENAME, timestamp);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("timestamp\tisp\tip\tlocation\tdownload_speed\tupload_speed\tidle_latency\tdownload_latency\tupload_latency\n");

            for (Result result: results) {
                String resultStr = result.getTimestamp() +
                        "\t" +
                        result.getIsp() +
                        "\t" +
                        result.getIp() +
                        "\t" +
                        result.getLocation() +
                        "\t" +
                        result.getDownloadSpeed() +
                        "\t" +
                        result.getUploadSpeed() +
                        "\t" +
                        result.getIdleLatency() +
                        "\t" +
                        result.getDownloadLatency() +
                        "\t" +
                        result.getUploadLatency() +
                        "\n" ;
                writer.write(resultStr);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
