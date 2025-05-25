package backend.academy.scrapper.link.exceptions;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.ExternalInitBase;
import backend.academy.scrapper.TestConfig;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        properties = {
            "resilience4j.retry.instances.defaultRetry.max-attempts=1",
            "resilience4j.ratelimiter.configs.defaultConfig.limit-for-period=3000"
        })
@AutoConfigureMockMvc
@Testcontainers
@Import(TestConfig.class)
public class AddLinkExceptionTests extends ExternalInitBase {
    @Test
    @DisplayName("Некорретный Tg-Chat-Id")
    public void test3() throws Exception {
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", "aslkdjf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("52"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"))
                .andExpect(
                        jsonPath("$.exceptionMessage")
                                .value(
                                        "Method parameter 'Tg-Chat-Id': Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"aslkdjf\""))
                .andExpect(jsonPath("$.stacktrace", instanceOf(List.class)))
                .andExpect(jsonPath("$.stacktrace", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Некорректный content")
    public void test52() throws Exception {
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("52"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.http.converter.HttpMessageNotReadableException"))
                .andExpect(
                        jsonPath("$.exceptionMessage")
                                .value(
                                        "JSON parse error: Cannot construct instance of `dto.AddLinkDTO` (although at least one Creator exists): no int/Int-argument constructor/factory method to deserialize from Number value (52)"))
                .andExpect(jsonPath("$.stacktrace", instanceOf(List.class)))
                .andExpect(jsonPath("$.stacktrace", hasSize(greaterThan(0))));
    }
}
