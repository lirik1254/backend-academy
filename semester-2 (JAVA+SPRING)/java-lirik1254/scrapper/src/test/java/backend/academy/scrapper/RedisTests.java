package backend.academy.scrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.link.AddLinkTestsBase;
import backend.academy.scrapper.services.update.UpdateLinkRedisService;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.SendUpdateDTO;
import dto.UpdateType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
        properties = {
            "resilience4j.retry.instances.defaultRetry.max-attempts=1",
            "resilience4j.ratelimiter.configs.defaultConfig.limit-for-period=3000"
        })
public class RedisTests extends AddLinkTestsBase {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private UpdateLinkRedisService updateLinkRedisService;

    @BeforeEach
    void flushRedisContainer() throws Exception {
        redis.execInContainer("redis-cli", "FLUSHALL");
    }

    @Test
    public void checkLinksCache() throws Exception {
        List<String> tags = java.util.List.of("tag1", "tag2");
        List<String> filters = java.util.List.of("filter1");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateType = UpdateType.ISSUE;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        when(gitHubInfoClient.getGithubContent("https://github.com/lirik1254/abTestRepo"))
                .thenReturn(java.util.List.of(new ContentDTO(
                        contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer)));

        try (var conn = redisConnectionFactory.getConnection()) {
            long before = conn.dbSize();
            assertEquals(0L, before, "Кэш до GET должен быть пуст");
        }

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        mockMvc.perform(get("/links").header("Tg-Chat-Id", 123).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        try (var conn = redisConnectionFactory.getConnection()) {
            long after = conn.dbSize();
            assertTrue(after > 0, "Кэш после GET должен содержать хотя бы одну запись");
        }

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        try (var conn = redisConnectionFactory.getConnection()) {
            long after = conn.dbSize();
            assertEquals(0L, after, "После добавления или удаления ссылки кеш инвалидируется");
        }
    }

    @Test
    public void checkTagsCache() throws Exception {
        List<String> tags = java.util.List.of("tag1", "tag2");
        List<String> filters = java.util.List.of("filter1");

        String githubLink = "https://github.com/lirik1254/abTestRepo";
        AddLinkDTO request = new AddLinkDTO(githubLink, tags, filters);

        String contentTitle = "Новое issue";
        UpdateType contentUpdateType = UpdateType.ISSUE;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2025-10-10";
        String contentAnswer = "i create new issue";

        when(gitHubInfoClient.getGithubContent("https://github.com/lirik1254/abTestRepo"))
                .thenReturn(java.util.List.of(new ContentDTO(
                        contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer)));

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        try (var conn = redisConnectionFactory.getConnection()) {
            long before = conn.dbSize();
            assertEquals(0L, before, "Кэш до GET должен быть пуст");
        }

        mockMvc.perform(get("/tags/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("tag1"))
                .andExpect(jsonPath("$[1]").value("tag2"));

        try (var conn = redisConnectionFactory.getConnection()) {
            long after = conn.dbSize();
            assertTrue(after > 0, "Кэш после GET должен содержать хотя бы одну запись");
        }

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        try (var conn = redisConnectionFactory.getConnection()) {
            long after = conn.dbSize();
            assertEquals(0L, after, "После добавления или удаления ссылки кеш инвалидируется");
        }
    }

    @Test
    public void addUpdateTest() {
        try (var conn = redisConnectionFactory.getConnection()) {
            long before = conn.dbSize();
            assertEquals(0L, before, "Кэш до добавления должен быть пуст");
        }

        updateLinkRedisService.addUpdate(123L, "52", new ContentDTO(UpdateType.ANSWER, "a", "a", "a", "a"));

        try (var conn = redisConnectionFactory.getConnection()) {
            long after = conn.dbSize();
            assertTrue(after > 0, "Кэш после add должен содержать запись");
        }
    }

    @Test
    public void clearTest() {
        updateLinkRedisService.addUpdate(123L, "52", new ContentDTO(UpdateType.ANSWER, "a", "a", "a", "a"));

        try (var conn = redisConnectionFactory.getConnection()) {
            long after = conn.dbSize();
            assertTrue(after > 0, "Кэш после add должен содержать запись");
        }

        updateLinkRedisService.clearUpdates(123L);

        try (var conn = redisConnectionFactory.getConnection()) {
            long before = conn.dbSize();
            assertEquals(0L, before, "Кэш после clear должен стереться");
        }
    }

    @Test
    public void getRedisContentCorrectTest() {
        updateLinkRedisService.addUpdate(123L, "52", new ContentDTO(UpdateType.ANSWER, "a", "b", "c", "d"));

        try (var conn = redisConnectionFactory.getConnection()) {
            long after = conn.dbSize();
            assertTrue(after > 0, "Кэш после add должен содержать запись");
        }

        List<SendUpdateDTO> updates = updateLinkRedisService.getUpdates(123L);
        assertEquals(1, updates.size());

        SendUpdateDTO sendUpdateDTO = updates.getFirst();
        ContentDTO contentDTO = sendUpdateDTO.content();

        assertEquals("52", sendUpdateDTO.url());
        assertEquals("a", contentDTO.title());
        assertEquals("b", contentDTO.userName());
        assertEquals("c", contentDTO.creationTime());
        assertEquals("d", contentDTO.answer());
        assertEquals(123L, sendUpdateDTO.chatId());
    }

    @Test
    public void getNonExistingContentTest() {
        updateLinkRedisService.addUpdate(123L, "52", new ContentDTO(UpdateType.ANSWER, "a", "b", "c", "d"));

        try (var conn = redisConnectionFactory.getConnection()) {
            long after = conn.dbSize();
            assertTrue(after > 0, "Кэш после add должен содержать запись");
        }

        List<SendUpdateDTO> updates = updateLinkRedisService.getUpdates(52L); // Другой юзер
        assertEquals(0, updates.size());
    }
}
