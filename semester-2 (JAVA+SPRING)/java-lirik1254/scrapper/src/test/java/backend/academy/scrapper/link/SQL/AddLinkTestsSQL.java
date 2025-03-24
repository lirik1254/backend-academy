package backend.academy.scrapper.link.SQL;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.scrapper.entities.SQL.Content;
import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.entities.SQL.LinkFilters;
import backend.academy.scrapper.entities.SQL.LinkTags;
import backend.academy.scrapper.entities.SQL.Url;
import backend.academy.scrapper.entities.SQL.Users;
import backend.academy.scrapper.link.AddLinkTestsBase;
import backend.academy.scrapper.repositories.SQL.ContentRepositorySQL;
import backend.academy.scrapper.repositories.SQL.FilterRepositorySQL;
import backend.academy.scrapper.repositories.SQL.LinkRepositorySQL;
import backend.academy.scrapper.repositories.SQL.TagRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UrlRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UsersRepositorySQL;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.UpdateType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class AddLinkTestsSQL extends AddLinkTestsBase {
    @Autowired
    public UsersRepositorySQL usersRepositorySQL;

    @Autowired
    public LinkRepositorySQL linkRepositorySQL;

    @Autowired
    public TagRepositorySQL tagRepositorySQL;

    @Autowired
    public FilterRepositorySQL filterRepositorySQL;

    @Autowired
    public UrlRepositorySQL urlRepositorySQL;

    @Autowired
    public ContentRepositorySQL contentRepositorySQL;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "SQL");
    }

    @Test
    @DisplayName("Тестирование добавления github ссылки")
    public void test1() throws Exception {
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

        performAddLinkRequest(request, 123L, githubLink, tags, filters);

        Users users = usersRepositorySQL.getByChatId(123L);
        assertNotNull(users);

        List<Link> userLinks = linkRepositorySQL.getUserLinks(users.chatId());
        assertEquals(1, userLinks.size());

        Link link = userLinks.getFirst();
        List<LinkTags> linkTags = tagRepositorySQL.getTagsByLinkId(link.linkId());
        assertEquals(2, linkTags.size());

        List<String> linkTagsString = linkTags.stream().map(LinkTags::text).toList();
        assertTrue(linkTagsString.containsAll(tags));

        List<LinkFilters> linkFilters = filterRepositorySQL.getFiltersByLinkId(link.linkId());
        assertEquals(1, linkFilters.size());

        assertTrue(linkFilters.getFirst().filters().contains("filter1"));

        Url url = urlRepositorySQL.getByUrl(githubLink);
        assertEquals("GITHUB", url.linkType());
        assertEquals(githubLink, url.url());

        List<Content> contents = contentRepositorySQL.getContentByUrlId(url.urlId());

        assertEquals(1, contents.size());

        Content content = contents.getFirst();
        assertEquals(contentTitle, content.title());
        assertEquals(contentAnswer, content.answer());
        assertEquals(contentCreationTime, content.creationTime());
        assertEquals("ISSUE", content.updatedType());
        assertEquals(contentUserName, content.userName());
    }

    @Test
    @DisplayName("Тестирование добавления stackoverflow ссылки")
    public void test2() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        String stackOverflowLink = "https://stackoverflow.com/questions/32617";
        AddLinkDTO request = new AddLinkDTO(stackOverflowLink, tags, filters);

        String contentTitle = "Новый ответ";
        UpdateType contentUpdateType = UpdateType.ANSWER;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2342342323";
        String contentAnswer = "i create new answer";

        when(stackOverflowClient.getSOContent(stackOverflowLink))
                .thenReturn(List.of(new ContentDTO(
                        contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer)));

        performAddLinkRequest(request, 123L, stackOverflowLink, tags, filters);

        Users users = usersRepositorySQL.getByChatId(123L);
        assertNotNull(users);

        List<Link> userLinks = linkRepositorySQL.getUserLinks(users.chatId());
        assertEquals(1, userLinks.size());

        Link link = userLinks.getFirst();
        List<LinkTags> linkTags = tagRepositorySQL.getTagsByLinkId(link.linkId());
        assertEquals(2, linkTags.size());

        List<String> linkTagsString = linkTags.stream().map(LinkTags::text).toList();
        assertTrue(linkTagsString.containsAll(tags));

        List<LinkFilters> linkFilters = filterRepositorySQL.getFiltersByLinkId(link.linkId());
        assertEquals(1, linkFilters.size());

        assertTrue(linkFilters.getFirst().filters().contains("filter1"));

        Url url = urlRepositorySQL.getByUrl(stackOverflowLink);
        assertEquals("STACKOVERFLOW", url.linkType());
        assertEquals(stackOverflowLink, url.url());

        List<Content> contents = contentRepositorySQL.getContentByUrlId(url.urlId());

        assertEquals(1, contents.size());

        Content content = contents.getFirst();
        assertEquals(contentTitle, content.title());
        assertEquals(contentAnswer, content.answer());
        assertEquals(contentCreationTime, content.creationTime());
        assertEquals("ANSWER", content.updatedType());
        assertEquals(contentUserName, content.userName());
    }

    @Test
    @DisplayName("Тестирование добавление одной и той же ссылки у одного и того же пользователя")
    public void test4() throws Exception {
        List<String> tags = List.of("tag1", "tag2");
        List<String> filters = List.of("filter1");

        String stackOverflowLink = "https://stackoverflow.com/questions/34534534";
        AddLinkDTO request = new AddLinkDTO(stackOverflowLink, tags, filters);

        String contentTitle = "Новый ответ";
        UpdateType contentUpdateType = UpdateType.ANSWER;
        String contentUserName = "lirik1254";
        String contentCreationTime = "2342342323";
        String contentAnswer = "i create new answer";

        when(stackOverflowClient.getSOContent(stackOverflowLink))
                .thenReturn(List.of(new ContentDTO(
                        contentUpdateType, contentTitle, contentUserName, contentCreationTime, contentAnswer)));

        performAddLinkRequest(request, 123L, stackOverflowLink, tags, filters);

        List<String> savedTags = tagRepositorySQL.getTagsByLinkId(1L).stream()
                .map(LinkTags::text)
                .toList();
        assertEquals(tags, savedTags);

        AddLinkDTO changeTagRequest = new AddLinkDTO(stackOverflowLink, List.of("52"), filters);

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeTagRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.url").value(request.link()))
                .andExpect(jsonPath("$.tags", hasItems("52")))
                .andExpect(jsonPath("$.filters", hasItems("filter1")));

        savedTags = tagRepositorySQL.getTagsByLinkId(1L).stream()
                .map(LinkTags::text)
                .toList();
        assertEquals(List.of("52"), savedTags);

        assertEquals(1, urlRepositorySQL.findAllWithPagination(0, 10).getTotalElements());
    }
}
