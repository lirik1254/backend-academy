package log.analyzer.report;

import log.analyzer.Format;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MdReportTest {

    @BeforeAll
    public static void createReport() {
        reportTestsUtils.createReport(Format.MARKDOWN);
    }

    @AfterAll
    public static void deleteReport() throws IOException {
        Files.delete(Path.of("report.md"));
        Files.delete(Path.of("statisticsFile.txt"));
    }


    @Test
    public void mdTextReportExistTest()  {
        File file = new File("report.md");
        assertTrue(file.exists());
    }

    @Test
    public void mdTextReportContentTest() throws IOException {
        ArrayList<String> reportFile = (ArrayList<String>) Files.readAllLines(Paths.get("report.md"));
        ArrayList<String> standardMdReport = (ArrayList<String>)
            Files.readAllLines(Paths.get("src/test/java/log/analyzer/standardMdReport.md"));
        assertEquals(reportFile.toString(), standardMdReport.toString());
    }
}
