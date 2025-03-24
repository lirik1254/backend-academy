package log.analyzer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import log.analyzer.nginx.parse.NginxRow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeRangeUtils {


    public ArrayList<NginxRow> getNginxTimeRange(LocalDate from, LocalDate to, ArrayList<NginxRow> nginxRows) {
        return (ArrayList<NginxRow>) nginxRows.stream()
            .filter(Objects::nonNull) // Проверка на null
            .filter(row -> {
                LocalDateTime dateTime = row.localDateTime();
                boolean isAfterFrom = from == null || !dateTime.isBefore(from.atStartOfDay());
                boolean isBeforeTo = to == null || !dateTime.isAfter(to.atStartOfDay());
                return isAfterFrom && isBeforeTo;
            })
            .collect(Collectors.toList());
    }
}
