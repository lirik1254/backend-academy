package backend.academy.scrapper.registration.exceptions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.ExternalInitBase;
import backend.academy.scrapper.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        properties = {
            "resilience4j.retry.instances.defaultRetry.max-attempts=1",
            "resilience4j.ratelimiter.configs.defaultConfig.limit-for-period=3000"
        })
@AutoConfigureMockMvc
@Testcontainers
@Import(TestConfig.class)
public class DeleteUserExceptionTests extends ExternalInitBase {
    @Test
    @DisplayName("Некорректное тело запроса")
    public void test3() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tg-chat/lakj").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Некорректные параметры запроса"))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.exceptionName")
                        .value("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException"))
                .andExpect(
                        jsonPath("$.exceptionMessage")
                                .value(
                                        "Method parameter 'id': Failed to convert value "
                                                + "of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"lakj\""))
                .andExpect(jsonPath("$.stacktrace").exists());
    }
}
