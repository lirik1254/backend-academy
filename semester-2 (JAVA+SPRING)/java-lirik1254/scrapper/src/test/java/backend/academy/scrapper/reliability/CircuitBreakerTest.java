package backend.academy.scrapper.reliability;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import dto.ContentDTO;
import general.RetryException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(
        properties = {
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.minimumNumberOfCalls=1",
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.slidingWindowSize=1"
        })
public class CircuitBreakerTest extends BaseReliability {
    @Test
    @DirtiesContext
    public void circuitBreakerTest() {
        doThrow(new RetryException("429"))
                .when(httpClient)
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));
        doThrow(new RetryException("429"))
                .when(kafkaClient)
                .sendUpdate(anyLong(), anyString(), ArgumentMatchers.any(ContentDTO.class));

        assertThrows(
                CallNotPermittedException.class,
                () -> updateLinkClientFacade.sendUpdate(
                        123L, "httpLink", new ContentDTO(null, null, null, null, null)));
    }
}
