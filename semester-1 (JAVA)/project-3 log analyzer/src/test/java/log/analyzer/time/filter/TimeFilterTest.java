package log.analyzer.time.filter;

import log.analyzer.TimeRangeUtils;
import log.analyzer.nginx.parse.NginxFileParseData;
import log.analyzer.nginx.parse.NginxParseData;
import log.analyzer.nginx.parse.NginxRow;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class TimeFilterTest {
    private static final String firstDayRow = "80.91.33.133 - - [17/May/2015:00:00:00 +0000] " +
        "\"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";
    private static final String secondDayRow = "80.91.33.133 - - [18/May/2015:00:00:00 +0000] " +
        "\"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";
    private static final String thirdDayRow = "80.91.33.133 - - [19/May/2015:00:00:00 +0000] " +
        "\"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";
    private static final String fourthDayRow = "80.91.33.133 - - [20/May/2015:00:00:00 +0000] " +
        "\"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";
    private static final String fifthDayRow = "80.91.33.133 - - [21/May/2015:00:00:00 +0000] " +
        "\"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";

    @BeforeAll
    public static void createDateFile() {
        try (FileWriter fileWriter = new FileWriter("dateFile.txt")) {
            fileWriter.write(firstDayRow + "\n");
            fileWriter.write(secondDayRow + "\n");
            fileWriter.write(thirdDayRow + "\n");
            fileWriter.write(fourthDayRow + "\n");
            fileWriter.write(fifthDayRow + "\n");
        } catch (IOException e) {
            System.out.println("Some file error");
        }
    }

    @AfterAll
    public static void deleteDateFile() throws IOException {
        Files.delete(Path.of("dateFile.txt"));
    }

    @Test
    public void getFromToTimeRangeRightDirection() {
        NginxParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(LocalDate.parse("2015-05-17"),
            LocalDate.parse("2015-05-19"), nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 3);
    }

    @Test
    public void getFromToTimeRangeBadDirection() {
        NginxParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(LocalDate.parse("2015-05-19"),
            LocalDate.parse("2015-05-17"), nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 0);
    }

    @Test
    public void getFromToTimeRangeNoDateInMas() {
        NginxParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(LocalDate.parse("2014-05-15"),
            LocalDate.parse("2014-05-17"), nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 0);
    }

    @Test
    public void getFromTimeRange() {
        NginxFileParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(LocalDate.parse("2015-05-19"), null, nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 3);
    }

    @Test
    public void getFromTimeRangeNoDateInMas() {
        NginxParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(LocalDate.parse("2016-05-19"),
            null, nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 0);
    }

    @Test
    public void getFromTimeRangeBorder() {
        NginxParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(null,
            LocalDate.parse("2015-05-17"), nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 1);
    }

    @Test
    public void getToTimeRange() {
        NginxParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(null,
            LocalDate.parse("2015-05-18"), nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 2);
    }

    @Test
    public void getToTimeRangeNoDateInMas() {
        NginxParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(null,
            LocalDate.parse("2014-05-25"), nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 0);
    }

    @Test
    public void getToTimeRangeBorder() {
        NginxParseData nginxFileParseData = new NginxFileParseData("dateFile.txt");
        ArrayList<NginxRow> nginxRows = TimeRangeUtils.getNginxTimeRange(null,
            LocalDate.parse("2015-05-17"), nginxFileParseData.getNginxList());
        assertEquals(nginxRows.size(), 1);
    }
}
