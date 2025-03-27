package backend.academy.scrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestClient;

@Configuration
@ComponentScan("general")
@RequiredArgsConstructor
public class OtherBeansConfiguration {
    private final ScrapperConfig scrapperConfig;

    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public NamedParameterJdbcTemplate template(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "baseUrl")
    public RestClient restClientBaseUrl() {
        return RestClient.builder().baseUrl(scrapperConfig.baseUrl()).build();
    }

    @Bean(name = "default")
    public RestClient restClientDefault() {
        return RestClient.create();
    }
}
