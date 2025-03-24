package log.analyzer.text.report;

import log.analyzer.Statistics;
import log.analyzer.WMArguments;

public class MdTextReport extends TextReport implements ReportGenerator {
    private static final StringBuilder GENERAL_INFORMATION = new StringBuilder("""
        #### Общая информация

        | Метрика | Значение |
        |:-:|:-:|
        | Файл(-ы) | %s |
        | Начальная дата | %s |
        | Конечная дата | %s |
        | Количество запросов | %d |
        | Средний размер ответа | %s |
        | 95р размера ответа | %s |
        | Среднее кол-во запросов в день | %d |
        \n""");

    private static final StringBuilder REQUESTED_RESOURCE_TOP = new StringBuilder("""
            #### Запрашиваемые ресурсы

            | Ресурс | Количество |
            |:-:|:-:|
            """);

    private static final StringBuilder RESPONSE_CODES_TOP = new StringBuilder("""
            \n#### Коды ответа

            | Код | Имя | Количество |
            |:-:|:-:|:-:|
            """);

    private static final StringBuilder IP_TOP = new StringBuilder("""
        \n#### IP адреса

        | IP | Количество запросов |
        |:-:|:-:|
        """);

    private final Statistics statistics;
    private final String[] paths;
    private final WMArguments wmArguments;

    @SuppressWarnings("MultipleStringLiterals")
    public MdTextReport(log.analyzer.Statistics statistics, String[] paths, WMArguments wmArguments) {
        this.statistics = statistics;
        this.paths = paths;
        this.wmArguments = wmArguments;
    }

    @Override
    @SuppressWarnings("MultipleStringLiterals")
    public void generateReport() {
        initDataByInput(statistics, paths, wmArguments);
        final String REQUESTED_RESOURCE_FORMAT = "| %s | %d |\n";
        final String RESPONSE_CODES_FORMAT = "| %s | %s | %d |\n";
        final String IP_FORMAT = "| %s | %d |\n";

        Report.FormatToAdd formatToAdd = new Report.FormatToAdd(
            REQUESTED_RESOURCE_FORMAT,
            RESPONSE_CODES_FORMAT,
            IP_FORMAT);

        Report.StatisticsFormat statisticsFormat = new Report.StatisticsFormat(
            GENERAL_INFORMATION,
            REQUESTED_RESOURCE_TOP,
            RESPONSE_CODES_TOP,
            IP_TOP);

        Report report = new Report(formatToAdd, statisticsFormat);

        String fileName = "report.md";

        writeReport(statistics, report, fileName, () -> "");
    }
}
