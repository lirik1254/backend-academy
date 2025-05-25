package backend.academy.scrapper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestClient;

@Configuration
@ComponentScan("general")
@RequiredArgsConstructor
public class OtherBeansConfiguration {
    private final ScrapperConfig scrapperConfig;

    @Value("${app.connection-timeout}")
    private Duration connectionTimeout;

    @Value("${app.read-timeout}")
    private Duration readTimeout;

    @Bean
    public NamedParameterJdbcTemplate template(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "baseUrl")
    public RestClient restClientBaseUrl() {
        return RestClient.builder()
                .baseUrl(scrapperConfig.baseUrl())
                .requestFactory(getClientHttpRequestFactory())
                .build();
    }

    @Bean(name = "default")
    public RestClient restClientDefault() {
        return RestClient.builder()
                .requestFactory(getClientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(readTimeout);
        factory.setConnectTimeout(connectionTimeout);
        return factory;
    }

    @Bean
    public ObjectMapper objectMapperWithDate() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }
}
