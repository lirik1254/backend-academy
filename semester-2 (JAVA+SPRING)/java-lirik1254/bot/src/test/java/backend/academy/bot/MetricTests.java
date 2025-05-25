package backend.academy.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetricTests {
    private SimpleMeterRegistry registry;
    private MessageMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new MessageMetrics(registry);
    }

    @Test
    void counterStartsAtZero() {
        Counter counter = registry.find("user_messages_total").counter();
        assertNotNull(counter, "Counter должен быть зарегистрирован");
        assertEquals(0.0, counter.count(), "Счётчик должен стартовать с 0");
    }

    @Test
    void incrementMessageIncreasesCounter() {
        metrics.incrementMessage();
        metrics.incrementMessage();
        metrics.incrementMessage();

        Counter counter = registry.find("user_messages_total").counter();
        assertNotNull(counter);
        assertEquals(3.0, counter.count(), "Счётчик должен увеличиться на 3");
    }
}
