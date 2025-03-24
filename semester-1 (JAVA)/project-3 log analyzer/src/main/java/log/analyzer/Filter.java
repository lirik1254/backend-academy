package log.analyzer;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import log.analyzer.nginx.parse.NginxRow;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Filter {

    private static final Map<FilterField, FilterFunction> FILTER_MAP = Map.of(
        FilterField.IP, value -> row -> row.ip().contains(value),
        FilterField.REQUESTED_RESOURCE, value -> row -> row.requestedResource().contains(value),
        FilterField.RESPONSE_CODE, value -> row -> row.responseCode().contains(value),
        FilterField.BODY_BYTES_SENT, value -> row -> row.bodyBytesSent().toString().contains(value),
        FilterField.CLIENT, value -> row -> row.client().contains(value)
    );

    public ArrayList<NginxRow> filter(FilterField field, String filterValue, ArrayList<NginxRow> nginxRows) {
        FilterFunction filterFunction = FILTER_MAP.getOrDefault(field, value -> row -> true);
        Predicate<NginxRow> predicate = filterFunction.apply(filterValue);
        return (ArrayList<NginxRow>) nginxRows.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }

    @FunctionalInterface
    interface FilterFunction {
        Predicate<NginxRow> apply(String filterValue);
    }
}
