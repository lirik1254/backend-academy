package log.analyzer.nginx.parse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NginxFileParseData implements NginxParseData {

    private final String path;

    public NginxFileParseData(String path) {
        this.path = path;
    }


    private void handleException(Exception e) {
        if (e instanceof  IOException) {
            log.error("No such file");
        }
        if (e instanceof SecurityException) {
            log.error("You don't have permission to access this file");
        }
        System.exit(0);
    }

    @Override
    public ArrayList<NginxRow> getNginxList() {
        ArrayList<NginxRow> nginxRows = new ArrayList<>();
        String[] paths = NginxParseDataUtils.getPathsMasFromLocalTemplate(path);
        for (String localPath : paths) {
            try (Stream<String> lines = Files.lines(Path.of(localPath))) {
                lines.forEach(s -> nginxRows.add(NginxParseDataUtils.getNginxDataFromLine(s)));
            } catch (Exception e) {
                log.error("no such file");
            }
        }
        return nginxRows;
    }
}
