package backend.academy.scrapper.tags;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.TestConfig;
import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.dbInitializeBase;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.UpdateType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
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
public class GetAllTagsTestsBase extends dbInitializeBase {
    @MockitoBean
    GitHubInfoClient gitHubInfoClient;

    @MockitoBean
    StackOverflowClient stackOverflowClient;

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

    @Test
    @DisplayName("Корректное получение всех тегов пользователя")
    public void test0() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateType = UpdateType.ISSUE;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        when(gitHubInfoClient.getGithubContent("https://github.com/lirik1254/abTestRepo"))
                .thenReturn(List.of(new ContentDTO(
                        contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer)));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/tags/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("tag1"))
                .andExpect(jsonPath("$[1]").value("tag2"));
    }

    @Test
    @DisplayName("У пользователя больше одной ссылки")
    public void test3() throws Exception {
        List<String> firstTags = List.of("tag1", "tag2");
        List<String> firstFilters = List.of("filter1");
        List<String> secondTags = List.of("tag3", "tag4");
        List<String> secondFilters = List.of("filter2");

        String firstLink = "https://github.com/lirik1254/abTestRepo";
        String secondLink = "https://stackoverflow.com/question/52";

        AddLinkDTO firstRequest = new AddLinkDTO(firstLink, firstTags, firstFilters);
        AddLinkDTO secondRequest = new AddLinkDTO(secondLink, secondTags, secondFilters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateType = UpdateType.ISSUE;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        ContentDTO addContentDTO =
                new ContentDTO(contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer);

        when(gitHubInfoClient.getGithubContent(firstLink)).thenReturn(List.of(addContentDTO));

        when(stackOverflowClient.getSOContent(secondLink)).thenReturn(List.of(addContentDTO));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        mockMvc.perform(get("/tags/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("tag1"))
                .andExpect(jsonPath("$[1]").value("tag2"))
                .andExpect(jsonPath("$[2]").value("tag3"))
                .andExpect(jsonPath("$[3]").value("tag4"));
    }

    @Test
    @DisplayName("Теги пользователя повторяются")
    public void test2() throws Exception {
        List<String> firstTags = List.of("tag1", "tag1");
        List<String> firstFilters = List.of("filter1");
        List<String> secondTags = List.of("tag1", "tag1");
        List<String> secondFilters = List.of("filter2");

        String firstLink = "https://github.com/lirik1254/abTestRepo";
        String secondLink = "https://stackoverflow.com/question/52";

        AddLinkDTO firstRequest = new AddLinkDTO(firstLink, firstTags, firstFilters);
        AddLinkDTO secondRequest = new AddLinkDTO(secondLink, secondTags, secondFilters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateType = UpdateType.ISSUE;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        ContentDTO addContentDTO =
                new ContentDTO(contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer);

        when(gitHubInfoClient.getGithubContent(firstLink)).thenReturn(List.of(addContentDTO));

        when(stackOverflowClient.getSOContent(secondLink)).thenReturn(List.of(addContentDTO));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        mockMvc.perform(get("/tags/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("tag1"));
    }
}
