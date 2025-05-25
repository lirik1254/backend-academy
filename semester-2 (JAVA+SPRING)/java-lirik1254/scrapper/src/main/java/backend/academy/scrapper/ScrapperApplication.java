package backend.academy.scrapper;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.config.ratelimiter.RateLimiterConfigurationParams;
import backend.academy.scrapper.config.retry.RetryConfigurationParams;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({
    ScrapperConfig.class,
    RetryConfigurationParams.class,
    RateLimiterConfigurationParams.class
})
@RequiredArgsConstructor
@EnableScheduling
@EnableCaching
@EnableRetry
public class ScrapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
