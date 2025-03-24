package backend.academy.samples.jcommander;

import com.beust.jcommander.JCommander;
import lombok.extern.log4j.Log4j2;

/**
 * JCommander is a command line parser.
 * It simplifies build command line interfaces for your application.
 * <p>
 * <a href="https://jcommander.org/">Library documentation</a>
 */
@Log4j2
public class JCommanderExample {
    public static void main(String[] args) {
        String[] sampleArgs = {"-log", "2", "-groups", "unit"};
        CliParams params = new CliParams();
        JCommander.newBuilder()
            .addObject(params)
            .build()
            .parse(sampleArgs);
        log.info(params);
    }
}
