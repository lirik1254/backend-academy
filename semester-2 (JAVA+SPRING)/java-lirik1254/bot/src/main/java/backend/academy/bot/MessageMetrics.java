package backend.academy.bot;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MessageMetrics {
    private final Counter messageCounter;

    public MessageMetrics(MeterRegistry meterRegistry) {
        this.messageCounter = meterRegistry.counter("user_messages_total");
    }

    public void incrementMessage() {
        messageCounter.increment();
    }
}
