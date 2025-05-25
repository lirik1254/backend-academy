package backend.academy.bot.reliability;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;

import backend.academy.bot.clients.TagsClient;
import general.RetryException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(
        properties = {
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.minimumNumberOfCalls=1",
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.slidingWindowSize=1"
        })
public class CircuitBreakerTest extends BaseReliability {
    @MockitoSpyBean
    private TagsClient tagsClient;

    @Test
    @DirtiesContext
    public void circuitBreakerTest() {
        doThrow(new RetryException("500")).when(tagsClient).getAllTags(anyLong());

        assertThrows(CallNotPermittedException.class, () -> tagsClient.getAllTags(123L));
    }
}
