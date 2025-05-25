package backend.academy.bot.reliability;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.bot.controllers.UpdateController;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ContentDTO;
import dto.UpdateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
        properties = {
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.minimumNumberOfCalls=20",
            "resilience4j.circuitbreaker.instances.baseCircuitBreaker.slidingWindowSize=20"
        })
@AutoConfigureMockMvc
public class RateLimiterTest extends BaseReliability {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UpdateController updateController;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void rateLimiterTest() throws Exception {
        when(updateController.update(any(UpdateDTO.class)))
                .thenReturn(String.valueOf(ResponseEntity.ok().build()));

        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ContentDTO(null, null, null, null, null))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ContentDTO(null, null, null, null, null))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ContentDTO(null, null, null, null, null))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ContentDTO(null, null, null, null, null))))
                .andExpect(status().isTooManyRequests());
    }
}
