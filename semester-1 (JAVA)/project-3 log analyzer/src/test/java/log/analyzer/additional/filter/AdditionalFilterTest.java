package log.analyzer.additional.filter;

import log.analyzer.Filter;
import log.analyzer.FilterField;
import log.analyzer.nginx.parse.NginxFileParseData;
import log.analyzer.nginx.parse.NginxParseData;
import log.analyzer.nginx.parse.NginxRow;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class AdditionalFilterTest {

    private static ArrayList<NginxRow> nginxRows;

    private static final String firstRow = "80.91.33.133 - - [17/May/2015:08:05:50 +0000]" +
        " \"GET /downloads/product_2 HTTP/1.1\" 500 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";
    private static final String secondRow = "173.203.139.108 - - [17/May/2015:08:05:03 +0000]" +
        " \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"";
    private static final String thirdRow = "80.91.33.133 - - [17/May/2015:08:05:35 +0000]" +
        " \"GET /downloads/product_1 HTTP/1.1\" 500 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"";
    private static final String fourthRow = "5.83.131.103 - - [17/May/2015:08:05:51 +0000]" +
        " \"GET /downloads/product_1 HTTP/1.1\" 200 490 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";
    private static final String fifthRow = "80.91.33.133 - - [18/May/2015:08:05:59 +0000]" +
        " \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.17)\"";

    @BeforeAll
    public static void createLogFile() {
        try (FileWriter fileWriter = new FileWriter("logFile.txt")) {
            fileWriter.write(firstRow + "\n");
            fileWriter.write(secondRow + "\n");
            fileWriter.write(thirdRow + "\n");
            fileWriter.write(fourthRow + "\n");
            fileWriter.write(fifthRow + "\n");
        } catch (IOException e) {
            System.out.println("Some file error");
        }
    }

    @BeforeEach
    public void initNginxRows() {
        NginxParseData nginxFileParseData = new NginxFileParseData("logFile.txt");
        nginxRows = nginxFileParseData.getNginxList();
    }

    @AfterAll
    public static void deleteLogFile() throws IOException {
        Files.delete(Path.of("logFile.txt"));
    }

    @Test
    public void ipFilterTest() {
        assertEquals(nginxRows.size(), 5);
        nginxRows = Filter.filter(FilterField.IP, "173", nginxRows);
        assertEquals(nginxRows.size(), 1);
        assertTrue(nginxRows.getFirst().ip().contains("173"));
    }

    @Test
    public void requestedResourceFilterTest() {
        assertEquals(5, nginxRows.size());
        nginxRows = Filter.filter(FilterField.REQUESTED_RESOURCE, "product_2", nginxRows);
        assertEquals(1, nginxRows.size());
        assertTrue(nginxRows.getFirst().requestedResource().contains("product_2"));
    }

    @Test
    public void responseCodeFilterTest() {
        assertEquals(5, nginxRows.size());
        nginxRows = Filter.filter(FilterField.RESPONSE_CODE, "500", nginxRows);
        assertEquals(2, nginxRows.size());
        assertTrue(nginxRows.getFirst().responseCode().contains("500"));
    }

    @Test
    public void bodyBytesSentFilterTest() {
        assertEquals(5, nginxRows.size());
        nginxRows = Filter.filter(FilterField.BODY_BYTES_SENT, "490", nginxRows);
        assertEquals(1, nginxRows.size());
        assertTrue(nginxRows.getFirst().bodyBytesSent().toString().contains("490"));
    }

    @Test
    public void clientFilterTest() {
        assertEquals(nginxRows.size(), 5);
        nginxRows = Filter.filter(FilterField.CLIENT, "10.17", nginxRows);
        assertEquals(nginxRows.size(), 1);
        assertTrue(nginxRows.getFirst().client().contains("10.17"));
    }
}
