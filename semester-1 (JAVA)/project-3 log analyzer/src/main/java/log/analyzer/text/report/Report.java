package log.analyzer.text.report;

public record Report(FormatToAdd formatToAdd, StatisticsFormat statisticsFormat) {
    public record FormatToAdd(
        String requestedResourcesFormat,
        String responseCodesFormat,
        String ipFormat
    ){}

    public record StatisticsFormat(
        StringBuilder generalInformation,
        StringBuilder requestedResourcesTop,
        StringBuilder responseCodesTop,
        StringBuilder ipTop
    ){}
}

