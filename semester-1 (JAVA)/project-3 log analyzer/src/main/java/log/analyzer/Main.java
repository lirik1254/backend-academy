package log.analyzer;

import java.io.IOException;
import java.net.URISyntaxException;
import lombok.experimental.UtilityClass;


@UtilityClass
public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        ConnectingClass connectingClass = new ConnectingClass(args);
        connectingClass.generateReport();
    }
}
