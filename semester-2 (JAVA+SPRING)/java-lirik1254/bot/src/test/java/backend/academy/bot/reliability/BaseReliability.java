package backend.academy.bot.reliability;

import backend.academy.bot.BaseConfigure;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class BaseReliability extends BaseConfigure {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.message-transport", () -> "HTTP");
        registry.add("resilience4j.circuitbreaker.instances.baseCircuitBreaker.slidingWindowType", () -> "COUNT_BASED");
        registry.add("resilience4j.circuitbreaker.instances.baseCircuitBreaker.failureRateThreshold", () -> "50");
        registry.add("resilience4j.circuitbreaker.instances.baseCircuitBreaker.waitDurationInOpenState", () -> "5s");
        registry.add(
                "resilience4j.circuitbreaker.instances.baseCircuitBreaker.permittedNumberOfCallsInHalfOpenState",
                () -> "2");

        // Retry
        registry.add("resilience4j.retry.instances.defaultRetry.max-attempts", () -> "3");
        registry.add("resilience4j.retry.instances.defaultRetry.wait-duration", () -> "500ms");
        registry.add("resilience4j.retry.instances.defaultRetry.exponential-backoff-multiplier", () -> "2");
        registry.add("resilience4j.retry.instances.defaultRetry.enable-exponential-backoff", () -> "true");
        registry.add("resilience4j.retry.instances.defaultRetry.retryable-status-pattern", () -> "5\\d{2}|429");

        // RateLimiter
        registry.add("resilience4j.ratelimiter.configs.defaultConfig.limit-for-period", () -> "3");
        registry.add("resilience4j.ratelimiter.configs.defaultConfig.timeout-duration", () -> "0s");
        registry.add("resilience4j.ratelimiter.configs.defaultConfig.limit-refresh-period", () -> "5s");
    }
}
