package backend.academy.bot.config.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RateLimiterConfiguration {
    private final RateLimiterConfigurationParams rateLimiterConfigurationParams;

    @Bean
    public RateLimiterConfig defaultRateLimiterConfig() {
        return RateLimiterConfig.custom()
                .timeoutDuration(rateLimiterConfigurationParams.timeoutDuration())
                .limitRefreshPeriod(rateLimiterConfigurationParams.limitRefreshPeriod())
                .limitForPeriod(rateLimiterConfigurationParams.limitForPeriod())
                .build();
    }
}
