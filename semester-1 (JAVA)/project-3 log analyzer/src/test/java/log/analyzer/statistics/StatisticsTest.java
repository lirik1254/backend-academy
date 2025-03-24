package log.analyzer.statistics;

import log.analyzer.Statistics;
import log.analyzer.nginx.parse.NginxFileParseData;
import log.analyzer.nginx.parse.NginxParseData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class StatisticsTest {

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

    NginxParseData nginxParseData = new NginxFileParseData("statisticsFile.txt");
    Statistics statistics = new Statistics(nginxParseData.getNginxList());


    @BeforeAll
    public static void createStatisticFile() {
        try (FileWriter fileWriter = new FileWriter("statisticsFile.txt")) {
            fileWriter.write(firstRow + "\n");
            fileWriter.write(secondRow + "\n");
            fileWriter.write(thirdRow + "\n");
            fileWriter.write(fourthRow + "\n");
            fileWriter.write(fifthRow + "\n");
        } catch (IOException e) {
            System.out.println("Some file error");
        }
    }

    @AfterAll
    public static void deleteStatisticFile() throws IOException {
        Files.delete(Path.of("statisticsFile.txt"));
    }

    @Test
    public void getBaseStatisticsTest() {
        assertEquals(5, statistics.totalRequestsNumber());
        assertEquals(490, statistics.p95());
        assertEquals(2, statistics.averageLogsPerDay());
        assertEquals(98, statistics.averageResponseSize());
    }

    @Test
    public void getIpCountMapTest() {
        assertEquals(3, statistics.ipCountMap().size());
        assertEquals(3, statistics.ipCountMap().get("80.91.33.133"));
        assertEquals(1, statistics.ipCountMap().get("5.83.131.103"));
        assertEquals(1, statistics.ipCountMap().get("173.203.139.108"));
    }

    @Test
    public void getResourceCountMapTest() {
        assertEquals(2, statistics.resourcesCountMap().size());
        assertEquals(4, statistics.resourcesCountMap().get("/downloads/product_1"));
        assertEquals(1, statistics.resourcesCountMap().get("/downloads/product_2"));
    }

    @Test
    public void getResponseCountMapTest() {
        assertEquals(3, statistics.responseCountMap().size());
        assertEquals(2, statistics.responseCountMap().get("500"));
        assertEquals(1, statistics.responseCountMap().get("200"));
        assertEquals(2, statistics.responseCountMap().get("304"));
    }
}
