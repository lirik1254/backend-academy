package backend.academy.scrapper.link;

import static backend.academy.scrapper.utils.ExceptionMessages.LINK_NOT_FOUND;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.ExternalInitBase;
import backend.academy.scrapper.TestConfig;
import backend.academy.scrapper.clients.GitHubInfoClient;
import dto.AddLinkDTO;
import dto.RemoveLinkRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        properties = {
            "resilience4j.retry.instances.defaultRetry.max-attempts=1",
            "resilience4j.ratelimiter.configs.defaultConfig.limit-for-period=3000"
        })
@AutoConfigureMockMvc
@Testcontainers
@Import(TestConfig.class)
public class DeleteLinkTestsBase extends ExternalInitBase {
    @MockitoBean
    protected GitHubInfoClient gitHubInfoClient;

    @BeforeEach
    public void clearDatabase() {
        try (Connection conn =
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP SCHEMA scrapper CASCADE");
                stmt.execute("CREATE SCHEMA scrapper");
            }

            runMigrations(conn);
        } catch (Exception e) {
            throw new RuntimeException("Database cleanup failed", e);
        }
    }

    protected void performAddLinkRequest(
            AddLinkDTO request,
            Long chatId,
            String expectedUrl,
            List<String> expectedTags,
            List<String> expectedFilters)
            throws Exception {
        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.any(int.class)))
                .andExpect(jsonPath("$.url").value(expectedUrl))
                .andExpect(jsonPath("$.tags", hasItems(expectedTags.toArray())))
                .andExpect(jsonPath("$.filters", hasItems(expectedFilters.toArray())));
    }

    protected void performDeleteLinkRequest(
            RemoveLinkRequest request,
            Long chatId,
            String expectedUrl,
            List<String> expectedTags,
            List<String> expectedFilters)
            throws Exception {
        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.any(int.class)))
                .andExpect(jsonPath("$.url").value(expectedUrl))
                .andExpect(jsonPath("$.tags", hasItems(expectedTags.toArray())))
                .andExpect(jsonPath("$.filters", hasItems(expectedFilters.toArray())));
    }

    protected void performDeleteLinkRequestNotFound(RemoveLinkRequest request, Long chatId) throws Exception {
        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", chatId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description").value(LINK_NOT_FOUND))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(
                        jsonPath("$.exceptionName").value("backend.academy.scrapper.exceptions.LinkNotFoundException"))
                .andExpect(jsonPath("$.exceptionMessage").value(LINK_NOT_FOUND))
                .andExpect(jsonPath("$.stacktrace").exists());
    }
}
