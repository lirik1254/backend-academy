package log.analyzer.report;

import log.analyzer.Format;
import log.analyzer.Statistics;
import log.analyzer.WMArguments;
import log.analyzer.nginx.parse.NginxFileParseData;
import log.analyzer.nginx.parse.NginxParseData;
import log.analyzer.text.report.AdocTextReport;
import log.analyzer.text.report.MdTextReport;
import lombok.experimental.UtilityClass;
import java.io.FileWriter;
import java.io.IOException;

@UtilityClass
public class reportTestsUtils {
    private final String firstRow = "80.91.33.133 - - [17/May/2015:08:05:50 +0000]" +
        " \"GET /downloads/product_2 HTTP/1.1\" 500 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";
    private final String secondRow = "173.203.139.108 - - [17/May/2015:08:05:03 +0000]" +
        " \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"";
    private final String thirdRow = "80.91.33.133 - - [17/May/2015:08:05:35 +0000]" +
        " \"GET /downloads/product_1 HTTP/1.1\" 500 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.16)\"";
    private final String fourthRow = "5.83.131.103 - - [17/May/2015:08:05:51 +0000]" +
        " \"GET /downloads/product_1 HTTP/1.1\" 200 490 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)\"";
    private final String fifthRow = "80.91.33.133 - - [18/May/2015:08:05:59 +0000]" +
        " \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.17)\"";

    private Statistics statistics;
    private String[] paths;
    private WMArguments wmArguments;


    public void createReport(Format format) {
        createLogFile();
        initializeData();
        switch (format) {
            case ADOC -> {
                new AdocTextReport(statistics, paths, wmArguments).generateReport();
            }
            case MARKDOWN -> {
                new MdTextReport(statistics, paths, wmArguments).generateReport();
            }
        }
    }

    private void initializeData() {
        NginxParseData nginxFileParseData = new NginxFileParseData("statisticsFile.txt");
        statistics = new Statistics(nginxFileParseData.getNginxList());
        String[] args = new String[] {"analyzer", "--path", "statisticsFile.txt"};
        paths = new String[] {args[2]};
        wmArguments = new WMArguments(args);

    }

    private void createLogFile() {
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



}
