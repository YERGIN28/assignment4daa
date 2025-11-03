package tests;

import com.daa.Generator.DatasetGenerator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class DatasetGeneratorTest {

    @Test
    void testGenerateCreatesFiles() throws Exception {
        String out = "data_test";

        Path p = Path.of(out);
        if (Files.exists(p)) {
            for (File f : p.toFile().listFiles()) f.delete();
            p.toFile().delete();
        }

        DatasetGenerator.generateAll(out);
        File dir = new File(out);
        assertTrue(dir.exists());

        assertEquals(9, dir.listFiles((d, name) -> name.endsWith(".json")).length);
    }
}

