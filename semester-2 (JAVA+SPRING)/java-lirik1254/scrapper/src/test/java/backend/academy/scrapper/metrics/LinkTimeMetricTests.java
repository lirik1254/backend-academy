package backend.academy.scrapper.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import backend.academy.scrapper.micrometer.link.time.LinkTimeMetric;
import backend.academy.scrapper.utils.LinkType;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LinkTimeMetricTests {
    private LinkTimeMetric linkTimeMetric;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        linkTimeMetric = new LinkTimeMetric(meterRegistry);
    }

    @Test
    void allTimersRegistered() {
        for (LinkType type : LinkType.values()) {
            Timer t = meterRegistry
                    .find("scrapper.duration")
                    .tag("type", type.name().toLowerCase())
                    .timer();
            assertNotNull(t, "Timer для типа " + type + " должен быть зарегистрирован");
        }
    }

    @Test
    void recordIncrementsCountAndSumAndMaxGithub() {
        Timer githubTimer = linkTimeMetric.getTimer(LinkType.GITHUB);

        githubTimer.record(100, TimeUnit.MILLISECONDS);
        githubTimer.record(200, TimeUnit.MILLISECONDS);
        githubTimer.record(150, TimeUnit.MILLISECONDS);

        assertEquals(3, githubTimer.count(), "Count должен равняться числу записей");

        double totalMs = githubTimer.totalTime(TimeUnit.MILLISECONDS);
        assertEquals(100 + 200 + 150, totalMs, 1e-6, "TotalTime должен быть суммой всех записей");

        double maxMs = githubTimer.max(TimeUnit.MILLISECONDS);
        assertEquals(200, maxMs, 1e-6, "Max должен быть наибольшей из записанных длительностей");
    }

    @Test
    void recordIncrementsCountAndSumAndMax() {
        Timer stackoverflowTimer = linkTimeMetric.getTimer(LinkType.STACKOVERFLOW);

        stackoverflowTimer.record(1000, TimeUnit.MILLISECONDS);
        stackoverflowTimer.record(2500, TimeUnit.MILLISECONDS);
        stackoverflowTimer.record(1500, TimeUnit.MILLISECONDS);

        assertEquals(3, stackoverflowTimer.count(), "Count должен равняться числу записей");

        double totalMs = stackoverflowTimer.totalTime(TimeUnit.MILLISECONDS);
        assertEquals(1000 + 2500 + 1500, totalMs, 1e-6, "TotalTime должен быть суммой всех записей");

        double maxMs = stackoverflowTimer.max(TimeUnit.MILLISECONDS);
        assertEquals(2500, maxMs, 1e-6, "Max должен быть наибольшей из записанных длительностей");
    }
}
