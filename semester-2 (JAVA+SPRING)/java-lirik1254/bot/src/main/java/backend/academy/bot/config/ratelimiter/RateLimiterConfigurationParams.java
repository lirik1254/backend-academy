package backend.academy.bot.config.ratelimiter;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("resilience4j.ratelimiter.configs.default-config")
public record RateLimiterConfigurationParams(
        Integer limitForPeriod, Duration timeoutDuration, Duration limitRefreshPeriod) {}
