package backend.academy.bot.config.retry;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "resilience4j.retry.instances.default-retry")
public record RetryConfigurationParams(String retryableStatusPattern) {}
