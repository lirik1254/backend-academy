package log.analyzer.text.report;

import log.analyzer.Statistics;
import log.analyzer.WMArguments;

public class AdocTextReport extends TextReport implements ReportGenerator {

    private static final StringBuilder GENERAL_INFORMATION = new StringBuilder("""
        === Общая информация

        |===
        | Метрика | Значение

        | Файл(-ы)
        | %s
        | Начальная дата
        | %s
        | Конечная дата
        | %s
        | Количество запросов
        | %d
        | Средний размер ответа
        | %s
        | p95
        | %s
        | Среднее кол-во запросов в день
        | %d
        |===\n\n""");

    private static final StringBuilder REQUESTED_RESOURCE_TOP = new StringBuilder("""
        === Запрашиваемые ресурсы

        |===
        | Ресурс | Количество

        """);

    private static final StringBuilder RESPONSE_CODES_TOP = new StringBuilder("""
        === Коды ответа

        |===
        | Код | Имя | Количество

        """);

    private static final StringBuilder IP_TOP = new StringBuilder("""
        === IP адреса

        |===
        | IP | Количество запросов

        """);

    private final Statistics statistics;
    private final String[] paths;
    private final WMArguments wmArguments;

    @SuppressWarnings("MultipleStringLiterals")
    public AdocTextReport(Statistics statistics, String[] paths, WMArguments wmArguments) {
        this.statistics = statistics;
        this.paths = paths;
        this.wmArguments = wmArguments;
    }

    @Override
    @SuppressWarnings("MultipleStringLiterals")
    public void generateReport() {
        initDataByInput(statistics, paths, wmArguments);
        String fileName = "report.adoc";
        final String REQUESTED_RESOURCE_FORMAT = "| %s\n| %d\n";
        final String RESPONSE_CODES_FORMAT = "| %s\n| %s\n| %d\n";
        final String IP_FORMAT = "| %s\n| %d\n";

        Report.FormatToAdd formatToAdd = new Report.FormatToAdd(
            REQUESTED_RESOURCE_FORMAT,
            RESPONSE_CODES_FORMAT,
            IP_FORMAT);

        Report.StatisticsFormat statisticsFormat = new Report.StatisticsFormat(GENERAL_INFORMATION,
            REQUESTED_RESOURCE_TOP,
            RESPONSE_CODES_TOP,
            IP_TOP);

        Report report = new Report(formatToAdd, statisticsFormat);

        writeReport(statistics, report, fileName, () -> "|===\n\n");
    }
}
