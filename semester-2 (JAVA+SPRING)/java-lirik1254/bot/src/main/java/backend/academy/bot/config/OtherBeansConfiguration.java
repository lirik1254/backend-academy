package backend.academy.bot.config;

import com.pengrad.telegrambot.TelegramBot;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
@ComponentScan(basePackages = "general")
public class OtherBeansConfiguration {
    private final BotConfig botConfig;

    @Value("${app.connection-timeout}")
    private Duration connectionTimeout;

    @Value("${app.read-timeout}")
    private Duration readTimeout;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botConfig.telegramToken());
    }

    @Bean
    public RestClient restClientDefault() {
        return RestClient.builder()
                .baseUrl(botConfig.baseUrl())
                .requestFactory(getClientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(readTimeout);
        factory.setConnectTimeout(connectionTimeout);
        return factory;
    }
}
