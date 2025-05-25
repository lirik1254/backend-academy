package backend.academy.scrapper.config.retry;

import general.RetryException;
import io.github.resilience4j.common.retry.configuration.RetryConfigCustomizer;
import jakarta.annotation.PostConstruct;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RetryConfiguration {
    private final RetryConfigurationParams retryConfigurationParams;
    private Pattern compiledStatusPattern;

    @PostConstruct
    public void init() {
        compiledStatusPattern = Pattern.compile(retryConfigurationParams.retryableStatusPattern());
    }

    @Bean
    public RetryConfigCustomizer defaultRetry() {
        return RetryConfigCustomizer.of("defaultRetry", builder -> {
            builder.retryOnException(throwable -> {
                        if (throwable instanceof RetryException) {
                            String errorCode = ((RetryException) throwable).getMessage();
                            return compiledStatusPattern.matcher(errorCode).matches();
                        }
                        return false;
                    })
                    .build();
        });
    }
}
