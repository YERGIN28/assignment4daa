package tests;

import com.daa.Generator.ReportGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class ReportGeneratorTest {

    @Test
    void testRunReport() throws Exception {

        ReportGenerator.generateAll("data", "reports");
        File csv = new File("reports/report.csv");
        File out = new File("reports/output.json");
        assertTrue(csv.exists());
        assertTrue(out.exists());

        assertTrue(csv.length() > 0);
        assertTrue(out.length() > 0);
    }
}

