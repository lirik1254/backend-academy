package backend.academy.scrapper.link;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.TestConfig;
import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.dbInitializeBase;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.UpdateType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
@Disabled
public class GetLinkTestsBase extends dbInitializeBase {
    @MockitoBean
    public GitHubInfoClient gitHubInfoClient;

    List<String> tags = List.of("tag1", "tag2");
    List<String> filters = List.of("filter1");

    String githubLink = "https://github.com/lirik1254/abTestRepo";
    AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

    String contentTitle = "Новое issue";
    UpdateType contentUpdateType = UpdateType.ISSUE;
    String contentUserName = "lirik1254";
    String contentCreationTime = "2025-10-10";
    String contentAnswer = "i create new issue";

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

    @BeforeEach
    public void initGithub() {
        when(gitHubInfoClient.getGithubContent("https://github.com/lirik1254/abTestRepo"))
                .thenReturn(List.of(new ContentDTO(
                        contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer)));
    }

    @Test
    @DisplayName("Тестирование корректного получения ссылок")
    public void test0() throws Exception {
        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/links").header("Tg-Chat-Id", 123).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.links[0].id", Matchers.any(int.class)))
                .andExpect(jsonPath("$.links[0].url").value("https://github.com/lirik1254/abTestRepo"))
                .andExpect(jsonPath("$.links[0].tags").isArray())
                .andExpect(jsonPath("$.links[0].tags[0]").value("tag1"))
                .andExpect(jsonPath("$.links[0].tags[1]").value("tag2"))
                .andExpect(jsonPath("$.links[0].filters").isArray())
                .andExpect(jsonPath("$.links[0].filters[0]").value("filter1"));
    }

    @Test
    @DisplayName("Тестирование получения 0 ссылок")
    public void test1() throws Exception {
        mockMvc.perform(get("/links").header("Tg-Chat-Id", 123).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.size").value(0));
    }
}
