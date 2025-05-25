package backend.academy.scrapper.micrometer.link.count;

import backend.academy.scrapper.utils.LinkType;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class LinkCountMetric {
    private final Map<LinkType, AtomicInteger> linkCounters = new EnumMap<>(LinkType.class);

    public LinkCountMetric(MeterRegistry registry) {
        for (LinkType type : LinkType.values()) {
            AtomicInteger counter = new AtomicInteger(0);
            linkCounters.put(type, counter);

            Gauge.builder("active_links", counter, AtomicInteger::get)
                    .description("Number of active links per type")
                    .tag("type", type.name().toLowerCase())
                    .register(registry);
        }
    }

    public void setLinkCount(LinkType linkType, int count) {
        AtomicInteger counter = linkCounters.get(linkType);
        if (counter != null) {
            counter.set(count);
        }
    }
}
