package log.analyzer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class WMArguments {

    @Parameter(names = {"--path"}, description = "Путь к файлу", required = true)
    private String path;

    @Parameter(names = {"--from"}, description = "С какой даты фильтровать")
    private String fromString;

    @Parameter(names = {"--to"}, description = "До какой даты фильтровать")
    private String toString;

    @Parameter(names = {"--format"}, description = "В каком формате выводить отчёт")
    private String format;

    @Parameter(names = {"--filter-field"}, description = "Поле, по которому нужно фильтровать")
    private String filterFieldString;

    @Parameter(names = {"--filter-value"}, description = "Значение для фильтрации")
    private String filterValue;

    @Parameter(description = "Главный параметр", required = true)
    private String mainParam;

    private LocalDate from;
    private LocalDate to;
    private FilterField filterField;

    public WMArguments(String[] args) {
        try {
            JCommander.newBuilder()
                .addObject(this)
                .build()
                .parse(args);

            initFields();

        } catch (ParameterException e) {
            log.error("Ошибка при разборе параметров: {}", e.getMessage());
            System.exit(1);
        }
    }

    private void initFields() {
        try {
            if (fromString != null) {
                from = LocalDate.parse(fromString);
            }
            if (toString != null) {
                to = LocalDate.parse(toString);
            }
            if (filterFieldString != null) {
                filterField = getFilterFieldByString(filterFieldString);
            }
        } catch (DateTimeParseException e) {
            log.error("Неверный формат даты: {}", e.getMessage());
            System.exit(1);
        }
    }

    private FilterField getFilterFieldByString(String field) {
        return switch (field.toLowerCase()) {
            case "ip" -> FilterField.IP;
            case "requested_resource" -> FilterField.REQUESTED_RESOURCE;
            case "response_code" -> FilterField.RESPONSE_CODE;
            case "body_bytes_sent" -> FilterField.BODY_BYTES_SENT;
            case "client" -> FilterField.CLIENT;
            default -> throw new IllegalArgumentException("Некорректное значение для --filter-field: " + field);
        };
    }
}
