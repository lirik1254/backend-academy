package backend.academy.scrapper;

import backend.academy.scrapper.services.LinkCheckService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public LinkCheckService linkCheckService() {
        return Mockito.mock(LinkCheckService.class);
    }
}
