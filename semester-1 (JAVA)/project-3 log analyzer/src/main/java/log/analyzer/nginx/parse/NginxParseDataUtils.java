package log.analyzer.nginx.parse;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.DirectoryScanner;

@UtilityClass
@Slf4j
public class NginxParseDataUtils {

    private final String logEntryRegex =
            "^(?<ip>\\d{1,3}(\\.\\d{1,3}){3}) - - \\[(?<time>[^]]+)] "
                + "\"(?<method>POST|GET|HEAD|PUT|DELETE|CONNECT|OPTIONS|TRACE|PATCH) (?<resource>/[^ ]*) [^\"]*\""
                + " (?<status>\\d{3}) (?<bytes>\\d+) \"[^\"]*\" \"(?<client>[^\"]*)\"$";
    private final Pattern logEntryPattern = Pattern.compile(logEntryRegex);

    private final DirectoryScanner scanner = new DirectoryScanner();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public String[] getPathsMasFromLocalTemplate(String path) {
        scanner.setBasedir(new File("."));
        scanner.setIncludes(new String[]{path});
        scanner.scan();
        return scanner.getIncludedFiles();
    }

    public NginxRow getNginxDataFromLine(String line) {
        Matcher matcher = logEntryPattern.matcher(line);
        if (matcher.matches()) {
            String ip = matcher.group("ip");
            String time = matcher.group("time");
            String requestedResource = matcher.group("resource");
            String responseCode = matcher.group("status");
            Integer bodyBytesSent = Integer.valueOf(matcher.group("bytes"));
            String client = matcher.group("client");

            LocalDateTime localTime = null;
            try {
                localTime = LocalDateTime.parse(time, formatter);
            } catch (Exception e) {
                log.error("Bad time parse");
            }

            return new NginxRow(ip, localTime, requestedResource, responseCode, bodyBytesSent, client);
        } else {
            return null;
        }
    }
}
