package log.analyzer.text.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import log.analyzer.Statistics;
import log.analyzer.WMArguments;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

@Slf4j
public abstract class TextReport {

    protected String files;
    protected String startDate;
    protected String endDate;
    protected Integer totalRequestsNumber;
    protected String averageResponseSize;
    protected String p95;
    protected Integer averageLogsPerDay;

    public void initDataByInput(Statistics statistics, String[] paths, WMArguments wmArguments) {
        files = String.join("\n\n", paths);
        startDate = wmArguments.from() == null ? "-" : wmArguments.from().toString();
        endDate = wmArguments.to() == null ? "-" : wmArguments.to().toString();
        totalRequestsNumber = statistics.totalRequestsNumber();
        averageResponseSize = statistics.averageResponseSize().toString() + "b";
        p95 = statistics.p95().toString() + "b";
        averageLogsPerDay = statistics.averageLogsPerDay();
    }

    protected void writeReport(Statistics statistics, Report report, String fileName, FormatInterface formatInterface) {

        StringBuilder generalInformation = report.statisticsFormat().generalInformation();
        StringBuilder requestedResourcesTop = report.statisticsFormat().requestedResourcesTop();
        StringBuilder responseCodesTop = report.statisticsFormat().responseCodesTop();
        StringBuilder ipTop = report.statisticsFormat().ipTop();

        String requestedResourceFormat = report.formatToAdd().requestedResourcesFormat();
        String responseCodesFormat = report.formatToAdd().responseCodesFormat();
        String ipFormat = report.formatToAdd().ipFormat();

        generalInformation = new StringBuilder(String.format(String.valueOf(generalInformation),
            files, startDate, endDate, totalRequestsNumber, averageResponseSize, p95, averageLogsPerDay));

        writeResourceTop3(statistics.resourcesCountMap(), requestedResourcesTop, requestedResourceFormat,
            formatInterface);
        writeResponseTop3(statistics, responseCodesTop, responseCodesFormat, formatInterface);
        writeIpTop3(statistics.ipCountMap(), ipTop, ipFormat, formatInterface);

        generalInformation.append(requestedResourcesTop).append(responseCodesTop).append(ipTop);

        createFile(generalInformation, fileName);
    }

    private void createFile(StringBuilder generalInformation, String fileName) {
        Path path = Path.of(fileName);
        try {
            Files.write(path, generalInformation.toString().getBytes(), WRITE, CREATE);
        } catch (IOException e) {
            log.error("No such directory");
            System.exit(0);
        }
    }

    private void writeIpTop3(
        HashMap<String, Integer> statistics,
        StringBuilder ipTop,
        String ipString,
        FormatInterface formatInterface
    ) {
        ArrayList<Map.Entry<String, Integer>> ipTop3 = TextReportUtils.getTopList(statistics);
        ipTop3.forEach(entry -> ipTop.append(String.format(ipString, entry.getKey(), entry.getValue())));
        ipTop.append(formatInterface.addFormat());
    }

    private void writeResponseTop3(
        Statistics statistics,
        StringBuilder responseCodesTop,
        String responseCodesString,
        FormatInterface formatInterface
    ) {
        ArrayList<Map.Entry<String, Integer>> responseTop3 = TextReportUtils.getTopList(statistics.responseCountMap());
        responseTop3.forEach(entry -> responseCodesTop.append(String.format(responseCodesString, entry.getKey(),
            HttpStatus.resolve(Integer.parseInt(entry.getKey())).getReasonPhrase(), entry.getValue())));
        responseCodesTop.append(formatInterface.addFormat());
    }

    private void writeResourceTop3(
        HashMap<String, Integer> statistics,
        StringBuilder requestedResourcesTop,
        String requestedResourceString,
        FormatInterface formatInterface
    ) {
        ArrayList<Map.Entry<String, Integer>> resourcesTop3 = TextReportUtils.getTopList(statistics);
        resourcesTop3.forEach(entry ->
            requestedResourcesTop.append(String.format(requestedResourceString, entry.getKey(), entry.getValue())));
        requestedResourcesTop.append(formatInterface.addFormat());
    }

    protected interface FormatInterface {
        String addFormat();
    }

}
