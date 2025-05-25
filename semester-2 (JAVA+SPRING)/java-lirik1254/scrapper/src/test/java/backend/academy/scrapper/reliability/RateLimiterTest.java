package backend.academy.scrapper.reliability;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.minimumNumberOfCalls=20",
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.slidingWindowSize=20"
        })
public class RateLimiterTest extends BaseReliability {

    @Test
    public void rateLimiterTest() throws Exception {
        mockMvc.perform(get("/tags/123")).andExpect(status().isOk());
        mockMvc.perform(get("/tags/123")).andExpect(status().isOk());
        mockMvc.perform(get("/tags/123")).andExpect(status().isOk());
        mockMvc.perform(get("/tags/123")).andExpect(status().isTooManyRequests());
    }
}
