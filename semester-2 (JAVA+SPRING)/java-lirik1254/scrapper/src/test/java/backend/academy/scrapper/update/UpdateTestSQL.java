package backend.academy.scrapper.update;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.entities.SQL.UserSettings;
import backend.academy.scrapper.link.AddLinkTestsBase;
import backend.academy.scrapper.repositories.SQL.LinkRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UserSettingsRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UsersRepositorySQL;
import backend.academy.scrapper.services.update.UpdateLinkService;
import backend.academy.scrapper.utils.SQL.ContentUtilsSQL;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.Settings;
import dto.TimeSettingsDTO;
import dto.UpdateType;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class UpdateTestSQL extends AddLinkTestsBase {
    protected static WireMockServer wireMockServer;

    @Autowired
    public ContentUtilsSQL contentUtilsSQL;

    @Autowired
    public LinkRepositorySQL linkRepositorySQL;

    @Autowired
    public UsersRepositorySQL usersRepositorySQL;

    @Autowired
    public UpdateLinkService updateLinkService;

    @Autowired
    public UserSettingsRepositorySQL userSettingsRepositorySQL;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterEach
    public void close() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "SQL");
        registry.add("app.message-transport", () -> "HTTP");
    }

    @Test
    @DisplayName("Контент не отправится, если фильтр равен нику автора контента")
    public void test1() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("user=lirik1254");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        Link link = linkRepositorySQL
                .getLinksByUrlAndChatId("https://github.com/lirik1254/abTestRepo", 123L)
                .getFirst();
        contentUtilsSQL.updateContentAndSend(
                link,
                List.of(new ContentDTO(UpdateType.ISSUE, "a", "a", "a", "a")),
                List.of(new ContentDTO(UpdateType.ISSUE, "b", "lirik1254", "b", "b")));

        wireMockServer.verify(0, postRequestedFor(urlEqualTo("/updates")));
    }

    @Test
    @DisplayName("Контент отправится, если фильтр не равен нику автора контента")
    public void test2() throws Exception {
        wireMockServer.stubFor(post(urlEqualTo("/updates"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")));

        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("user=lirik1254");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        Link link = linkRepositorySQL
                .getLinksByUrlAndChatId("https://github.com/lirik1254/abTestRepo", 123L)
                .getFirst();
        contentUtilsSQL.updateContentAndSend(
                link,
                List.of(new ContentDTO(UpdateType.ISSUE, "a", "a", "a", "a")),
                List.of(new ContentDTO(UpdateType.ISSUE, "b", "b", "b", "b")));

        wireMockServer.verify(postRequestedFor(urlEqualTo("/updates")));
    }

    @Test
    @DisplayName("Контент отправится, если установлена настройка IMMEDIATELY")
    public void test3() throws Exception {
        wireMockServer.stubFor(post(urlEqualTo("/updates"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")));

        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("user=lirik1254");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        performAddLinkRequest(request, 123L, githubLink, tags, filters);
        UserSettings userSettings =
                userSettingsRepositorySQL.getUserSettings(123L).getFirst();

        assertEquals(Settings.IMMEDIATELY.name(), userSettings.notifyMood());

        Link link = linkRepositorySQL
                .getLinksByUrlAndChatId("https://github.com/lirik1254/abTestRepo", 123L)
                .getFirst();
        contentUtilsSQL.updateContentAndSend(
                link,
                List.of(new ContentDTO(UpdateType.ISSUE, "a", "a", "a", "a")),
                List.of(new ContentDTO(UpdateType.ISSUE, "b", "b", "b", "b")));

        wireMockServer.verify(postRequestedFor(urlEqualTo("/updates")));
    }

    @Test
    @DisplayName("Контент не отправится, если установлена настройка BY_TIME и время ещё не наступило")
    public void test4() throws Exception {
        wireMockServer.stubFor(post(urlEqualTo("/updates"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")));

        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("user=lirik1254");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        mockMvc.perform(MockMvcRequestBuilders.post("/time")
                        .header("Tg-Chat-Id", 123L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TimeSettingsDTO(
                                Settings.BY_TIME, LocalTime.now().minusHours(2)))))
                .andExpect(status().isOk());

        UserSettings userSettings =
                userSettingsRepositorySQL.getUserSettings(123L).getFirst();

        assertEquals(Settings.BY_TIME.name(), userSettings.notifyMood());

        Link link = linkRepositorySQL
                .getLinksByUrlAndChatId("https://github.com/lirik1254/abTestRepo", 123L)
                .getFirst();
        contentUtilsSQL.updateContentAndSend(
                link,
                List.of(new ContentDTO(UpdateType.ISSUE, "a", "a", "a", "a")),
                List.of(new ContentDTO(UpdateType.ISSUE, "b", "b", "b", "b")));

        wireMockServer.verify(0, postRequestedFor(urlEqualTo("/updates")));
    }

    @DisplayName("Контент отправится, если установлена настройка BY_TIME и время наступило")
    @Test
    public void test5() throws Exception {
        wireMockServer.stubFor(post(urlEqualTo("/updates"))
                .withRequestBody(matching(".*"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")));

        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("user=lirik1254");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        mockMvc.perform(MockMvcRequestBuilders.post("/time") // Ставим настройки, чтоб обновления накапливались в кеше
                        .header("Tg-Chat-Id", 123L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TimeSettingsDTO(
                                Settings.BY_TIME, LocalTime.now().minusHours(2)))))
                .andExpect(status().isOk());

        Link link = linkRepositorySQL
                .getLinksByUrlAndChatId("https://github.com/lirik1254/abTestRepo", 123L)
                .getFirst();
        contentUtilsSQL.updateContentAndSend(
                link,
                List.of(new ContentDTO(UpdateType.ISSUE, "a", "a", "a", "a")),
                List.of(new ContentDTO(UpdateType.ISSUE, "b", "b", "b", "b"))); // Отправляем контент в кеш

        mockMvc.perform(MockMvcRequestBuilders.post("/time") // Меняем настройки на текущее время
                        .header("Tg-Chat-Id", 123L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TimeSettingsDTO(
                                Settings.BY_TIME, LocalTime.now().truncatedTo(ChronoUnit.MINUTES)))))
                .andExpect(status().isOk());

        UserSettings userSettings =
                userSettingsRepositorySQL.getUserSettings(123L).getFirst();
        assertEquals(Settings.BY_TIME.name(), userSettings.notifyMood());

        updateLinkService.sendNotifications(); // Ожидаем отправку /updates

        wireMockServer.verify(postRequestedFor(urlEqualTo("/updates")));
    }
}
