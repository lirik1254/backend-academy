package backend.academy.bot;

import backend.academy.bot.config.BotConfig;
import backend.academy.bot.config.ratelimiter.RateLimiterConfigurationParams;
import backend.academy.bot.config.retry.RetryConfigurationParams;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableConfigurationProperties({BotConfig.class, RetryConfigurationParams.class, RateLimiterConfigurationParams.class})
@EnableRetry
public class BotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
