package backend.academy.scrapper.link;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.TestConfig;
import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.dbInitializeBase;
import dto.AddLinkDTO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestConfig.class)
public class AddLinkTestsBase extends dbInitializeBase {
    @MockitoBean
    public StackOverflowClient stackOverflowClient;

    @MockitoBean
    public GitHubInfoClient gitHubInfoClient;

    @BeforeEach
    public void clearDatabase() {
        try (Connection conn =
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP SCHEMA public CASCADE");
                stmt.execute("CREATE SCHEMA public");
                stmt.execute("GRANT ALL ON SCHEMA public TO public");
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
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.url").value(expectedUrl))
                .andExpect(jsonPath("$.tags", hasItems(expectedTags.toArray())))
                .andExpect(jsonPath("$.filters", hasItems(expectedFilters.toArray())));
    }
}
