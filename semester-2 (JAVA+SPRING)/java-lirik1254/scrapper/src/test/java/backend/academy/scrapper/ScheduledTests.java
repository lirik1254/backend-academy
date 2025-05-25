package backend.academy.scrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.update.UpdateLinkClientHTTP;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.services.LinkCheckService;
import backend.academy.scrapper.services.interfaces.UpdateChecker;
import backend.academy.scrapper.utils.ORM.UpdateCheckerUtilsORM;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.UpdateType;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        properties = {
            "resilience4j.retry.instances.defaultRetry.max-attempts=1",
            "resilience4j.ratelimiter.configs.defaultConfig.limit-for-period=3000"
        })
@AutoConfigureMockMvc
@Testcontainers
public class ScheduledTests extends ExternalInitBase {

    @MockitoBean
    public GitHubInfoClient gitHubInfoClient;

    @MockitoBean
    public UpdateLinkClientHTTP updateLinkClientHTTP;

    @MockitoBean
    public LinkCheckService linkCheckService;

    @Autowired
    UrlRepositoryORM urlRepositoryORM;

    @Autowired
    UpdateChecker updateChecker;

    @MockitoSpyBean
    UpdateCheckerUtilsORM updateCheckerUtilsORM;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "ORM");
        registry.add("app.message-transport", () -> "HTTP");
    }

    String firstUserGithubLink = "https://github.com/lirik1254/abTestRepo";
    String secondUserGithubLink = "https://github.com/anotherAuthor/anotherLink";
    ContentDTO firstRequestNewContent = new ContentDTO(UpdateType.ISSUE, "title", "lirik1254", "2020-10-10", "issue");
    ContentDTO secondRequestNewContent = new ContentDTO(UpdateType.PR, "title", "lirik1254", "2019-08-10", "comment");

    @BeforeEach
    public void clearDatabaseAndFill() throws Exception {
        try (Connection conn =
                DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP SCHEMA scrapper CASCADE");
                stmt.execute("CREATE SCHEMA scrapper");
                stmt.execute("GRANT ALL ON SCHEMA scrapper TO public");
            }

            runMigrations(conn);
        } catch (Exception e) {
            throw new RuntimeException("Database cleanup failed", e);
        }

        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("user=52");

        AddLinkDTO firstRequest = new AddLinkDTO(firstUserGithubLink, tags, filters);
        AddLinkDTO secondRequest = new AddLinkDTO(secondUserGithubLink, tags, filters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateType = UpdateType.ISSUE;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        ContentDTO contentDTO =
                new ContentDTO(contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer);

        when(gitHubInfoClient.getGithubContent(any())).thenReturn(List.of(contentDTO));

        mockMvc.perform(MockMvcRequestBuilders.post("/tg-chat/5050")); // Регистрируем пользователя с чатом 5050

        mockMvc.perform(MockMvcRequestBuilders.post("/links") // Добавляем 1 github ссылку для пользователя с id 123
                .header("Tg-Chat-Id", 123)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)));

        mockMvc.perform(MockMvcRequestBuilders.post("/links") // Добавляем 2 github ссылку для пользователя с id 5252
                .header("Tg-chat-Id", 5252)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)));

        when(gitHubInfoClient.getGithubContent(eq(firstUserGithubLink)))
                .thenReturn(List.of(contentDTO, firstRequestNewContent));
        when(gitHubInfoClient.getGithubContent(eq(secondUserGithubLink)))
                .thenReturn(List.of(contentDTO, secondRequestNewContent));
    }

    @Test
    @DisplayName("Обновления отправляются в разных потоках")
    @DirtiesContext
    public void test2() throws Exception {
        doNothing().when(updateCheckerUtilsORM).processUrlPage(any());

        updateChecker.checkUpdates();

        ArgumentCaptor<List<Url>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(updateCheckerUtilsORM, timeout(1000).times(2)).processUrlPage(argumentCaptor.capture());

        List<List<Url>> allValues = argumentCaptor.getAllValues();
        assertEquals(2, allValues.size());

        List<String> urlStrings = allValues.stream()
                .flatMap(List::stream) // Превращаем List<List<Url>> в Stream<Url>
                .map(Url::url) // Применяем метод url() к каждому Url
                .toList();

        Assertions.assertTrue(urlStrings.contains(firstUserGithubLink));
        Assertions.assertTrue(urlStrings.contains(secondUserGithubLink));
    }

    @Test
    @DisplayName("Обновления отправляются только пользователям, которые следят за ссылкой")
    @DirtiesContext
    public void test1() throws Exception {
        doNothing().when(updateLinkClientHTTP).sendUpdate(any(), any(), any());

        updateChecker.checkUpdates();

        // 1 пользователю отправляется обновление с 1 ссылкой
        Mockito.verify(updateLinkClientHTTP, timeout(1000))
                .sendUpdate(123L, firstUserGithubLink, firstRequestNewContent);

        // 2 пользователю со 2 ссылкой
        Mockito.verify(updateLinkClientHTTP, timeout(1000))
                .sendUpdate(5252L, secondUserGithubLink, secondRequestNewContent);

        // 5050L который не подписывался на ссылки, ничего не отправляется
        Mockito.verify(updateLinkClientHTTP, times(0)).sendUpdate(eq(5050L), anyString(), any());
    }
}
