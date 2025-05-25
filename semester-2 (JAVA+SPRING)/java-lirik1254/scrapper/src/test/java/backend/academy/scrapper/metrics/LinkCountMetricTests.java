package backend.academy.scrapper.metrics;

import static org.junit.jupiter.api.Assertions.*;

import backend.academy.scrapper.micrometer.link.count.LinkCountMetric;
import backend.academy.scrapper.utils.LinkType;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LinkCountMetricTests {
    private LinkCountMetric linkCountMetric;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        linkCountMetric = new LinkCountMetric(meterRegistry);
    }

    @Test
    public void linkCountStartFromZero() {
        Gauge gauge = meterRegistry.find("active_links").gauge();
        assertNotNull(gauge);
        assertEquals(0.0, gauge.value());
    }

    @Test
    public void setLinkCountGithubTest() {
        linkCountMetric.setLinkCount(LinkType.GITHUB, 15);

        Gauge gauge = meterRegistry
                .find("active_links")
                .tag("type", LinkType.GITHUB.name().toLowerCase())
                .gauge();
        assertNotNull(gauge);
        assertEquals(15.0, gauge.value());
    }

    @Test
    public void setLinkCountStackoverflowTest() {
        linkCountMetric.setLinkCount(LinkType.STACKOVERFLOW, 8);

        Gauge gauge = meterRegistry
                .find("active_links")
                .tag("type", LinkType.STACKOVERFLOW.name().toLowerCase())
                .gauge();

        assertNotNull(gauge);
        assertEquals(8.0, gauge.value());
    }
}
