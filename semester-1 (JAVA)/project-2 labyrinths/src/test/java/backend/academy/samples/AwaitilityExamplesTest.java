package backend.academy.samples;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import static org.awaitility.Awaitility.given;

/**
 * Awaitility is a DSL that allows you to express expectations
 * of an asynchronous system in a concise and easy to read manner.
 * <p>
 * <a href="https://github.com/awaitility/awaitility/wiki/Usage">Library documentation</a>
 */
public class AwaitilityExamplesTest {
    @Test
    public void awaitExample() {
        given()
            .ignoreException(IllegalStateException.class)
            .await()
            .atLeast(Duration.ofMillis(1))
            .atMost(Duration.ofSeconds(5))
            .until(() -> {
                Thread.sleep(10);
                return true;
            });
    }
}
