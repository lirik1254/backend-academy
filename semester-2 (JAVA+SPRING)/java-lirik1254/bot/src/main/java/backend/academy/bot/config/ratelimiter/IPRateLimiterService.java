package backend.academy.bot.config.ratelimiter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class IPRateLimiterService {
    private final RateLimiterRegistry registry;
    private final RateLimiterConfig defaultConfig;
    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    public IPRateLimiterService(RateLimiterConfig defaultConfig) {
        this.registry = RateLimiterRegistry.of(defaultConfig);
        this.defaultConfig = defaultConfig;
    }

    public RateLimiter getLimiter(String key) {
        return limiters.computeIfAbsent(key, k -> registry.rateLimiter(k, defaultConfig));
    }
}
