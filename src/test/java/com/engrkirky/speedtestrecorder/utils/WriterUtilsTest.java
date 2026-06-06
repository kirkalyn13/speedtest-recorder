package com.engrkirky.speedtestrecorder.utils;

import com.engrkirky.speedtestrecorder.model.Result;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WriterUtilsTest {

    @Test
    void writeResults_createsCSVFile() {
        List<Result> results = List.of(
                new Result(
                        "2026-01-01T00:00:00Z",
                        "Spectrum", "35.144.158.14", "Kingsport, TN",
                        1036.72, 39.36, 27.0, 34.0, 26.0)
        );

        WriterUtils.writeResults(results);

        File[] files = new File(".").listFiles(
                f -> f.getName().startsWith("speedtest-results") && f.getName().endsWith(".csv")
        );

        assertNotNull(files);
        assertTrue(files.length > 0);

        // cleanup
        for (File f : files) f.delete();
    }

    @Test
    void writeResults_writesCorrectHeaders() throws Exception {
        List<Result> results = List.of(
                new Result(
                        "2026-01-01T00:00:00Z",
                        "Spectrum", "35.144.158.14", "Kingsport, TN",
                        1036.72, 39.36, 27.0, 34.0, 26.0)
        );

        WriterUtils.writeResults(results);

        File[] files = new File(".").listFiles(
                f -> f.getName().startsWith("speedtest-results") && f.getName().endsWith(".csv")
        );

        assertNotNull(files);
        String content = new String(java.nio.file.Files.readAllBytes(files[0].toPath()));
        assertTrue(content.contains("timestamp\tisp\tip\tlocation"));

        for (File f : files) f.delete();
    }
}
