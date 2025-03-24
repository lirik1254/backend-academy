package log.analyzer;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import log.analyzer.nginx.parse.NginxRow;
import lombok.Getter;



public class Statistics {
    @Getter
    private final Integer totalRequestsNumber;
    @Getter
    private final Integer averageResponseSize;

    private BigInteger responseSizeSum = BigInteger.valueOf(0);

    @Getter
    private final Integer averageLogsPerDay;

    @Getter
    private final HashMap<String, Integer> resourcesCountMap = new LinkedHashMap<>();
    @Getter
    private final HashMap<String, Integer> responseCountMap = new LinkedHashMap<>();
    @Getter
    private final HashMap<String, Integer> ipCountMap = new HashMap<>();


    private final ArrayList<Integer> bodyBytesSendList = new ArrayList<>();

    @Getter
    private final Integer p95;

    public Statistics(ArrayList<NginxRow> nginxRows) {
        totalRequestsNumber = nginxRows.size();

        nginxRows.forEach(row -> {
            putResourceCount(row.requestedResource());
            putResponseCount(row.responseCode());
            putIpCount(row.ip());
            responseSizeSum = responseSizeSum.add(BigInteger.valueOf(row.bodyBytesSent()));
            bodyBytesSendList.add(row.bodyBytesSent());
        });

        averageResponseSize = totalRequestsNumber == 0 ? 0
            : Integer.parseInt(
            String.valueOf(responseSizeSum.divide(BigInteger.valueOf(totalRequestsNumber))));

        p95 = calculateP95(bodyBytesSendList);

        averageLogsPerDay = getAverageLogsPerDay(nginxRows);
    }

    private void putResourceCount(String requestedResource) {
        resourcesCountMap.putIfAbsent(requestedResource, 0);
        resourcesCountMap.put(requestedResource, resourcesCountMap.get(requestedResource) + 1);
    }

    private void putResponseCount(String responseCount) {
        responseCountMap.putIfAbsent(responseCount, 0);
        responseCountMap.put(responseCount, responseCountMap.get(responseCount) + 1);
    }

    private void putIpCount(String ip) {
        ipCountMap.putIfAbsent(ip, 0); // Если ключ еще не существует, вставляем его с начальным значением 0
        ipCountMap.put(ip, ipCountMap.get(ip) + 1);
    }

    @SuppressWarnings("MagicNumber")
    private Integer calculateP95(ArrayList<Integer> bodyBytesSendList) {
        Collections.sort(bodyBytesSendList);
        double percentile = 0.95;
        return bodyBytesSendList.isEmpty() ? 0 : bodyBytesSendList.get((int) (bodyBytesSendList.size() * percentile));
    }

    private Integer getAverageLogsPerDay(ArrayList<NginxRow> nginxRows) {
        if (!nginxRows.isEmpty()) {
            LocalDate firstDate = nginxRows.getFirst().localDateTime().toLocalDate();
            LocalDate lastDate = nginxRows.getLast().localDateTime().toLocalDate();

            Integer daysValue = Math.toIntExact(ChronoUnit.DAYS.between(firstDate, lastDate)) + 1;
            return totalRequestsNumber / daysValue;
        } else {
            return 0;
        }
    }



}
