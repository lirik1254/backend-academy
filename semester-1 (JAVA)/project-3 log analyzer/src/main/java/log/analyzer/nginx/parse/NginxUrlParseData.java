package log.analyzer.nginx.parse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class NginxUrlParseData implements NginxParseData {
    private final URI uri;

    public NginxUrlParseData(URI uri) {
        this.uri = uri;
    }


    @Override
    public ArrayList<NginxRow> getNginxList() {
        ArrayList<NginxRow> nginxRows = new ArrayList<>();
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String[] lines = response.body().split("\n");
            Arrays.stream(lines).forEach(s -> nginxRows.add(NginxParseDataUtils.getNginxDataFromLine(s)));
        } catch (Exception e) {
            log.error("Error receiving response");
        }
        return nginxRows;
    }
}
