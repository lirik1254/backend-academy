package log.analyzer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import log.analyzer.nginx.parse.NginxFileParseData;
import log.analyzer.nginx.parse.NginxParseData;
import log.analyzer.nginx.parse.NginxParseDataUtils;
import log.analyzer.nginx.parse.NginxRow;
import log.analyzer.nginx.parse.NginxUrlParseData;
import log.analyzer.text.report.AdocTextReport;
import log.analyzer.text.report.MdTextReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;

@Slf4j
public class ConnectingClass {

    UrlValidator urlValidator = new UrlValidator();
    WMArguments wmArguments;

    public ConnectingClass(String[] args) {
        this.wmArguments = new WMArguments(args);
    }


    private ArrayList<NginxRow> parseNginxData(WMArguments wmArguments, String path, boolean isUrl) {
        try {
            NginxParseData nginxParseData = isUrl
                ? new NginxUrlParseData(new URI(path))
                : new NginxFileParseData(path);

            return TimeRangeUtils.getNginxTimeRange(wmArguments.from(), wmArguments.to(),
                nginxParseData.getNginxList());
        } catch (URISyntaxException e) {
            log.error("Wrong path");
            System.exit(0);
            return null;
        }
    }

    private void generateReport(Statistics statistics, WMArguments wmArguments, Format format) {
        String[] sources = urlValidator.isValid(wmArguments.path())
            ? new String[]{wmArguments.path()}
            : NginxParseDataUtils.getPathsMasFromLocalTemplate(wmArguments.path());

        if (format.equals(Format.ADOC)) {
            new AdocTextReport(statistics, sources, wmArguments).generateReport();
        } else {
            new MdTextReport(statistics, sources, wmArguments).generateReport();
        }
    }

    public void generateReport() {
        boolean isUrl = urlValidator.isValid(wmArguments.path());

        Format format = getFormat(wmArguments);

        ArrayList<NginxRow> nginxRows = getNginxRows(wmArguments, isUrl);

        Statistics statistics = new Statistics(nginxRows);

        generateReport(statistics, wmArguments, format);
    }

    private static Format getFormat(WMArguments wmArguments) {
        boolean isAdoc = "adoc".equals(wmArguments.format());
        Format format = isAdoc ? Format.ADOC : Format.MARKDOWN;
        return format;
    }

    private ArrayList<NginxRow> getNginxRows(WMArguments wmArguments, boolean isUrl) {
        ArrayList<NginxRow> nginxRows = parseNginxData(wmArguments, wmArguments.path(), isUrl);
        nginxRows = getFilteredNginxRows(wmArguments, nginxRows);
        return nginxRows;
    }

    @SuppressWarnings("ParameterAssignment")
    private static ArrayList<NginxRow> getFilteredNginxRows(WMArguments wmArguments, ArrayList<NginxRow> nginxRows) {
        if (wmArguments.filterField() != null && wmArguments.filterValue() != null) {
            nginxRows = Filter.filter(wmArguments.filterField(), wmArguments.filterValue(), nginxRows);
        }
        return nginxRows;
    }

}
