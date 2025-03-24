package log.analyzer.parse;

import log.analyzer.TestUtils;
import log.analyzer.nginx.parse.NginxParseDataUtils;
import log.analyzer.nginx.parse.NginxRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class ParseTest {

    private final String IP = "80.91.33.133";
    private final String LOCAL_DATE_TIME = "2015-05-17T08:05:50";
    private final String REQUESTED_RESOURCE = "/downloads/product_1";
    private final String RESPONSE_CODE = "304";
    private final Integer BODY_BYTES_SENT = 0;
    private final String CLIENT = "Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.22)";


    @Test
    public void parseCorrLine() {
        NginxRow nginxRow = NginxParseDataUtils.getNginxDataFromLine(TestUtils.CORRECT_LINE);
        assertEquals(IP, nginxRow.ip());
        assertEquals(LOCAL_DATE_TIME, nginxRow.localDateTime().toString());
        assertEquals(REQUESTED_RESOURCE, nginxRow.requestedResource());
        assertEquals(RESPONSE_CODE, nginxRow.responseCode());
        assertEquals(BODY_BYTES_SENT, nginxRow.bodyBytesSent());
        assertEquals(CLIENT, nginxRow.client());
    }


}
