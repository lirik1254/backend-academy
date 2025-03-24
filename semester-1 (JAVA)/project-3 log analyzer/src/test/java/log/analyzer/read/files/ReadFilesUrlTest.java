package log.analyzer.read.files;

import log.analyzer.nginx.parse.NginxFileParseData;
import log.analyzer.nginx.parse.NginxParseData;
import log.analyzer.nginx.parse.NginxUrlParseData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.*;
public class ReadFilesUrlTest {

    final String CORRECT_URL = "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs";
    final String INCORRECT_URL = "skldjf";

    @Test
    public void readFilesByCorrURL() throws URISyntaxException {
        NginxParseData nginxParseData = new NginxUrlParseData(new URI(CORRECT_URL));
        assertFalse(nginxParseData.getNginxList().isEmpty());
    }

    @Test
    public void readFilesByIncURL() throws URISyntaxException {
        NginxParseData nginxParseData = new NginxUrlParseData(new URI(INCORRECT_URL));
        assertTrue(nginxParseData.getNginxList().isEmpty());
    }
}

