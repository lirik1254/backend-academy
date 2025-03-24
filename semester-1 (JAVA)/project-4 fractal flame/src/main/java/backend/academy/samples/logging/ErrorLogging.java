package backend.academy.samples.logging;

import lombok.extern.log4j.Log4j2;

/**
 * Log4j is a Java logging framework. The default {@link java.util.logging.Logger} is not so flexible and convenient.
 * <p>
 * <a href="https://logging.apache.org/log4j/2.x/manual/index.html">Project manual</a>
 */
@Log4j2
public class ErrorLogging {
    public static void main(String[] args) {
        try {
            getData();
        } catch (IllegalArgumentException e) {
            log.error("unexpected error", e);
        }

    }

    static void getData() throws IllegalArgumentException {
        throw new IllegalArgumentException("Sorry IllegalArgumentException!");
    }

}
