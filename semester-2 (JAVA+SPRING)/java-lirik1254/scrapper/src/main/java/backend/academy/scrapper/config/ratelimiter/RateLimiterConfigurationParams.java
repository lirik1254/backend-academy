package backend.academy.scrapper.config.ratelimiter;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "resilience4j.ratelimiter.configs.default-config")
public record RateLimiterConfigurationParams(
        Integer limitForPeriod, Duration timeoutDuration, Duration limitRefreshPeriod) {}
