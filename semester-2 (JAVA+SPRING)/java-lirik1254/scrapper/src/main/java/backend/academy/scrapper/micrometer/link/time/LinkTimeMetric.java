package backend.academy.scrapper.micrometer.link.time;

import backend.academy.scrapper.utils.LinkType;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public class LinkTimeMetric {
    @Getter
    private final MeterRegistry registry;

    private final Map<LinkType, Timer> timers = new EnumMap<>(LinkType.class);

    public LinkTimeMetric(MeterRegistry registry) {
        this.registry = registry;
        for (LinkType type : LinkType.values()) {
            Timer timer = Timer.builder("scrapper.duration")
                    .tag("type", type.name().toLowerCase())
                    .publishPercentileHistogram(true)
                    .publishPercentiles(0.5, 0.95, 0.99)
                    .register(registry);
            timers.put(type, timer);
        }
    }

    public Timer getTimer(LinkType type) {
        Timer timer = timers.get(type);
        if (timer == null) {
            throw new IllegalArgumentException("Unknown LinkType: " + type);
        }
        return timer;
    }
}
