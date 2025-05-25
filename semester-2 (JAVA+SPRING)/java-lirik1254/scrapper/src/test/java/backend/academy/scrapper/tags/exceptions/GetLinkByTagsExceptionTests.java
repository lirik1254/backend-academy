package backend.academy.scrapper.tags.exceptions;

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
public class GetLinkByTagsExceptionTests extends ExternalInitBase {

    @Test
    @DisplayName("chatId некорректен")
    public void test7() throws Exception {
        mockMvc.perform(post("/link-tags/adsf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("52"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"))
                .andExpect(
                        jsonPath("$.exceptionMessage")
                                .value(
                                        "Method parameter 'chatId': Failed to convert value "
                                                + "of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"adsf\""))
                .andExpect(jsonPath("$.stacktrace").exists());
    }

    @Test
    @DisplayName("Некорректное тело запроса")
    public void test8() throws Exception {
        mockMvc.perform(post("/link-tags/52")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("asdfasdfasdfsd")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.http.converter.HttpMessageNotReadableException"))
                .andExpect(jsonPath("$.exceptionMessage")
                        .value("JSON parse error: Cannot construct instance of"
                                + " `java.util.ArrayList` (although at least one Creator exists): "
                                + "no String-argument constructor/factory method to deserialize from "
                                + "String value ('asdfasdfasdfsd')"))
                .andExpect(jsonPath("$.stacktrace").exists());
    }
}
