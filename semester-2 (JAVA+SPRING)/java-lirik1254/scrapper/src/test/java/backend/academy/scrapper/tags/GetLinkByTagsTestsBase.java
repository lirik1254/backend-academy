package backend.academy.scrapper.tags;

import static org.mockito.Mockito.when;
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
public class GetLinkByTagsTestsBase extends dbInitializeBase {

    @MockitoBean
    GitHubInfoClient gitHubInfoClient;

    @MockitoBean
    StackOverflowClient stackOverflowClient;

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

    @Test
    @DisplayName("Тег только в 1 ссылке")
    public void test0() throws Exception {
        List<String> firstTags = List.of("tag1", "tag2");
        List<String> firstFilters = List.of("filter1");
        List<String> secondTags = List.of("tag3", "tag4");
        List<String> secondFilters = List.of("filter2");

        String firstLink = "https://github.com/lirik1254/abTestRepo";
        String secondLink = "https://stackoverflow.com/question/52";

        AddLinkDTO firstRequest = new AddLinkDTO(firstLink, firstTags, firstFilters);
        AddLinkDTO secondRequest = new AddLinkDTO(secondLink, secondTags, secondFilters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateTypeGH = UpdateType.ISSUE;
        UpdateType contentUpdateTypeSO = UpdateType.ANSWER;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        ContentDTO addContentDTOSO =
                new ContentDTO(contentUpdateTypeSO, contentTitle, contentUserName, contentCreationTime, contentAnswer);
        ContentDTO addContentDTOGH =
                new ContentDTO(contentUpdateTypeGH, contentTitle, contentUserName, contentCreationTime, contentAnswer);

        when(gitHubInfoClient.getGithubContent(firstLink)).thenReturn(List.of(addContentDTOGH));

        when(stackOverflowClient.getSOContent(secondLink)).thenReturn(List.of(addContentDTOSO));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        mockMvc.perform(post("/link-tags/123")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("tag1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    @DisplayName("Тег в обоих ссылках")
    public void test2() throws Exception {
        List<String> firstTags = List.of("tag1", "tag2");
        List<String> firstFilters = List.of("filter1");
        List<String> secondTags = List.of("tag1", "tag4");
        List<String> secondFilters = List.of("filter2");

        String firstLink = "https://github.com/lirik1254/abTestRepo";
        String secondLink = "https://stackoverflow.com/question/52";

        AddLinkDTO firstRequest = new AddLinkDTO(firstLink, firstTags, firstFilters);
        AddLinkDTO secondRequest = new AddLinkDTO(secondLink, secondTags, secondFilters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateTypeGH = UpdateType.ISSUE;
        UpdateType contentUpdateTypeSO = UpdateType.ANSWER;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        ContentDTO addContentDTOSO =
                new ContentDTO(contentUpdateTypeSO, contentTitle, contentUserName, contentCreationTime, contentAnswer);
        ContentDTO addContentDTOGH =
                new ContentDTO(contentUpdateTypeGH, contentTitle, contentUserName, contentCreationTime, contentAnswer);

        when(gitHubInfoClient.getGithubContent(firstLink)).thenReturn(List.of(addContentDTOGH));

        when(stackOverflowClient.getSOContent(secondLink)).thenReturn(List.of(addContentDTOSO));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        mockMvc.perform(post("/link-tags/123")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("tag1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    @DisplayName("Ввод и существующих, и несуществующих тегов")
    public void test5() throws Exception {
        List<String> firstTags = List.of("tag1", "tag2");
        List<String> firstFilters = List.of("filter1");
        List<String> secondTags = List.of("tag3", "tag4");
        List<String> secondFilters = List.of("filter2");

        String firstLink = "https://github.com/lirik1254/abTestRepo";
        String secondLink = "https://stackoverflow.com/question/52";

        AddLinkDTO firstRequest = new AddLinkDTO(firstLink, firstTags, firstFilters);
        AddLinkDTO secondRequest = new AddLinkDTO(secondLink, secondTags, secondFilters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateTypeGH = UpdateType.ISSUE;
        UpdateType contentUpdateTypeSO = UpdateType.ANSWER;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        ContentDTO addContentDTOSO =
                new ContentDTO(contentUpdateTypeSO, contentTitle, contentUserName, contentCreationTime, contentAnswer);
        ContentDTO addContentDTOGH =
                new ContentDTO(contentUpdateTypeGH, contentTitle, contentUserName, contentCreationTime, contentAnswer);

        when(gitHubInfoClient.getGithubContent(firstLink)).thenReturn(List.of(addContentDTOGH));

        when(stackOverflowClient.getSOContent(secondLink)).thenReturn(List.of(addContentDTOSO));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        mockMvc.perform(post("/link-tags/123")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("tag1", "tag2", "tag3", "tag4", "tag5"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    @DisplayName("Нет ссылок по введённым тегам")
    public void test6() throws Exception {
        List<String> firstTags = List.of("tag1", "tag2");
        List<String> firstFilters = List.of("filter1");

        String firstLink = "https://github.com/lirik1254/abTestRepo";

        AddLinkDTO firstRequest = new AddLinkDTO(firstLink, firstTags, firstFilters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateType = UpdateType.ISSUE;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        ContentDTO addContentDTO =
                new ContentDTO(contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer);

        when(gitHubInfoClient.getGithubContent(firstLink)).thenReturn(List.of(addContentDTO));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        mockMvc.perform(post("/link-tags/123")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of("фафыва"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").isArray())
                .andExpect(jsonPath("$.size").value(0));
    }
}
