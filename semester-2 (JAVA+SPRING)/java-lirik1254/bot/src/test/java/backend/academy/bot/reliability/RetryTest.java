package backend.academy.bot.reliability;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

import backend.academy.bot.clients.TagsClient;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import general.RetryException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(
        properties = {
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.minimumNumberOfCalls=20",
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.slidingWindowSize=20"
        })
public class RetryTest extends BaseReliability {

    @MockitoSpyBean
    private TagsClient tagsClient;

    @Test
    @DirtiesContext
    void shouldRetryExactly3TimesAndThrowException() {
        doThrow(new RetryException("500")).when(tagsClient).getAllTags(anyLong());

        RetryException exception = assertThrows(RetryException.class, () -> tagsClient.getAllTags(123L));

        Mockito.verify(tagsClient, Mockito.times(3)).getAllTags(eq(123L));
    }

    @Test
    @DirtiesContext
    public void afterSuccessNoRetry() {
        wireMockServer.stubFor(get(urlEqualTo("/tags/123"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(500))
                .willSetStateTo("Second Attempt"));

        String jsonAnswer = """
        [
           "my",
           "tag"
        ]""";

        wireMockServer.stubFor(get(urlEqualTo("/tags/123"))
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Second Attempt")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        tagsClient.getAllTags(123L);

        wireMockServer.verify(2, getRequestedFor(urlEqualTo("/tags/123")));

        Mockito.verify(tagsClient, Mockito.times(2)).getAllTags(eq(123L));
    }

    @Test
    @DirtiesContext
    public void retryOn429() {
        doThrow(new RetryException("429")).when(tagsClient).getAllTags(anyLong());

        assertThrows(RetryException.class, () -> tagsClient.getAllTags(123L));

        Mockito.verify(tagsClient, Mockito.times(3)).getAllTags(anyLong());
    }

    @Test
    @DirtiesContext
    public void noRetryOnNot429AndNot5xx() {
        doThrow(new RetryException("404")).when(tagsClient).getAllTags(anyLong());

        assertThrows(RetryException.class, () -> tagsClient.getAllTags(123L));

        Mockito.verify(tagsClient, Mockito.times(1)).getAllTags(anyLong());
    }
}
