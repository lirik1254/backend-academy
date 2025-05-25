package backend.academy.scrapper.reliability;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import dto.ContentDTO;
import dto.UpdateType;
import general.RetryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(
        properties = {
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.minimumNumberOfCalls=20",
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.slidingWindowSize=20"
        })
public class RetryTest extends BaseReliability {
    protected static WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DirtiesContext
    public void threeRetryAndFallbackToAnotherTransport() {
        doThrow(new RetryException("500"))
                .when(httpClient)
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));
        doThrow(new RetryException("500"))
                .when(kafkaClient)
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));

        assertThrows(
                RetryException.class,
                () -> updateLinkClientFacade.sendUpdate(
                        123L, "httpLink", new ContentDTO(null, null, null, null, null)));

        Mockito.verify(httpClient, Mockito.times(3))
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));

        Mockito.verify(kafkaClient, Mockito.times(3))
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));
    }

    @Test
    @DirtiesContext
    public void twoRetryAndSuccessNoFallback() {
        wireMockServer.stubFor(post(urlEqualTo("/updates"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(500))
                .willSetStateTo("Second Attempt"));

        wireMockServer.stubFor(post(urlEqualTo("/updates"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Second Attempt")
                .willReturn(aResponse().withStatus(500))
                .willSetStateTo("Third Attempt"));

        wireMockServer.stubFor(post(urlEqualTo("/updates"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Third Attempt")
                .willReturn(aResponse().withStatus(200)));

        assertDoesNotThrow(() -> updateLinkClientFacade.sendUpdate(
                123L,
                "https://github.com/lirik1254/abTestRepo",
                new ContentDTO(UpdateType.COMMENT, "Title", "user", "123", "answer")));

        wireMockServer.verify(3, postRequestedFor(urlEqualTo("/updates")));

        Mockito.verify(httpClient, Mockito.times(3))
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));

        Mockito.verify(kafkaClient, Mockito.times(0))
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));
    }

    @Test
    @DirtiesContext
    public void retryOn429() {
        doThrow(new RetryException("429"))
                .when(httpClient)
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));
        doThrow(new RetryException("429"))
                .when(kafkaClient)
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));

        assertThrows(
                RetryException.class,
                () -> updateLinkClientFacade.sendUpdate(
                        123L, "httpLink", new ContentDTO(null, null, null, null, null)));

        Mockito.verify(httpClient, Mockito.times(3))
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));

        Mockito.verify(kafkaClient, Mockito.times(3))
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));
    }

    @Test
    @DirtiesContext
    public void noRetryOnNot429AndNot5xx() {
        doThrow(new RetryException("404"))
                .when(httpClient)
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));
        doThrow(new RetryException("404"))
                .when(kafkaClient)
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));

        assertThrows(
                RetryException.class,
                () -> updateLinkClientFacade.sendUpdate(
                        123L, "httpLink", new ContentDTO(null, null, null, null, null)));

        Mockito.verify(httpClient, Mockito.times(1))
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));

        Mockito.verify(kafkaClient, Mockito.times(1))
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));
    }
}
