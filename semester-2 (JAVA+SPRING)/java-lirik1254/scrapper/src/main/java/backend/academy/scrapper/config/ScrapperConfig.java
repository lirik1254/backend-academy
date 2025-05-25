package backend.academy.scrapper.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ScrapperConfig(
        @NotEmpty String githubToken, StackOverflowCredentials stackOverflow, @NotEmpty String baseUrl, int batchSize) {
    public static final int THREAD_NUMBER = Runtime.getRuntime().availableProcessors();

    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}
}
