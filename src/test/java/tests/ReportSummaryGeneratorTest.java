package tests;

import com.daa.Generator.ReportSummaryGenerator;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportSummaryGeneratorTest {

    @Test
    void testGenerateSummary() throws Exception {
        // assumes data/ exists with json files
        ReportSummaryGenerator.generateSummary("data", "report/report_summary.csv");
        assertTrue(Files.exists(Path.of("report/report_summary.csv")));
        assertTrue(Files.size(Path.of("report/report_summary.csv")) > 0);
    }
}
