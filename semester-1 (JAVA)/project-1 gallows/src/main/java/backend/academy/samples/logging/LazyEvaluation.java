package backend.academy.samples.logging;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LazyEvaluation {
    public static void main(String[] args) {
        log.debug("Hello from Log4j 2");

        // in old days, we need to check the log level to increase performance
        if (log.isDebugEnabled()) {
            log.debug("{}", number());
        }
        // with Java 8, we can do this, no need to check the log level (lazy message evaluation)
        log.debug("{}", () -> number());
    }

    static int number() {
        return 5;
    }

}
