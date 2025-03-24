package log.analyzer.report;

import log.analyzer.Format;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdocReportTest {
    @BeforeAll
    public static void createReport() {
        reportTestsUtils.createReport(Format.ADOC);
    }

    @AfterAll
    public static void deleteReport() throws IOException {
        Files.delete(Path.of("report.adoc"));
        Files.delete(Path.of("statisticsFile.txt"));
    }

    @Test
    public void adocTextReportExistTest()  {
        File file = new File("report.adoc");
        assertTrue(file.exists());
    }

    @Test
    public void adocTextReportContentTest() throws IOException {
        ArrayList<String> reportFile = (ArrayList<String>) Files.readAllLines(Paths.get("report.adoc"));
        ArrayList<String> standardAdocReport = (ArrayList<String>)
            Files.readAllLines(Paths.get("src/test/java/log/analyzer/standardAdocReport.adoc"));
        assertEquals(reportFile.toString(), standardAdocReport.toString());
    }
}
