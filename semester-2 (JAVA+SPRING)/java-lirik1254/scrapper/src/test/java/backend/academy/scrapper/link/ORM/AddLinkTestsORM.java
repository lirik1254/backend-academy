package backend.academy.scrapper.link.ORM;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import backend.academy.scrapper.entities.JPA.Content;
import backend.academy.scrapper.entities.JPA.GithubContent;
import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.StackOverflowContent;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.entities.JPA.User;
import backend.academy.scrapper.link.AddLinkTestsBase;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UsersRepositoryORM;
import backend.academy.scrapper.utils.LinkType;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.UpdateType;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class AddLinkTestsORM extends AddLinkTestsBase {

    @Autowired
    UrlRepositoryORM urlRepositoryORM;

    @Autowired
    UsersRepositoryORM usersRepositoryORM;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "ORM");
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

        List<User> users = usersRepositoryORM.findAll();
        assertEquals(1, users.size());

        User user = users.getFirst();
        assertEquals(123, user.chatId());
        assertEquals(1, user.links().size());

        Link link = user.links().getFirst();
        assertEquals(tags, link.tags());
        assertEquals(filters, link.filters());

        Url url = link.url();
        assertEquals(LinkType.GITHUB, url.linkType());
        assertEquals(githubLink, url.url());

        List<Content> contents = url.contents();
        assertEquals(1, contents.size());

        Content content = contents.getFirst();
        assertEquals(contentTitle, content.title());
        assertEquals(contentAnswer, content.answer());
        assertEquals(contentCreationTime, content.creationTime());
        assertEquals(contentUserName, content.userName());

        assertInstanceOf(GithubContent.class, content, "Content должен быть экземпляром GithubContent");
        GithubContent githubContent = (GithubContent) content;
        assertEquals(contentUpdateType, githubContent.updatedType());
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

        List<User> users = usersRepositoryORM.findAll();
        assertEquals(1, users.size());

        User user = users.getFirst();
        assertEquals(123, user.chatId());
        assertEquals(1, user.links().size());

        Link link = user.links().getFirst();
        assertEquals(tags, link.tags());
        assertEquals(filters, link.filters());

        Url url = link.url();
        assertEquals(LinkType.STACKOVERFLOW, url.linkType());
        assertEquals(stackOverflowLink, url.url());

        List<Content> contents = url.contents();
        assertEquals(1, contents.size());

        Content content = contents.getFirst();
        assertEquals(contentTitle, content.title());
        assertEquals(contentAnswer, content.answer());
        assertEquals(contentCreationTime, content.creationTime());
        assertEquals(contentUserName, content.userName());

        assertInstanceOf(StackOverflowContent.class, content, "Content должен быть экземпляром StackOverflowContent");
        StackOverflowContent stackOverflowContent = (StackOverflowContent) content;
        assertEquals(contentUpdateType, stackOverflowContent.updatedType());
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

        List<String> savedTags =
                usersRepositoryORM.findAll().getFirst().links().getFirst().tags();
        assertEquals(tags, savedTags);

        AddLinkDTO changeTagRequest = new AddLinkDTO(stackOverflowLink, List.of("52"), filters);

        mockMvc.perform(post("/links")
                        .header("Tg-Chat-Id", 123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeTagRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.any(int.class)))
                .andExpect(jsonPath("$.url").value(request.link()))
                .andExpect(jsonPath("$.tags", hasItems("52")))
                .andExpect(jsonPath("$.filters", hasItems("filter1")));

        savedTags = usersRepositoryORM.findAll().getFirst().links().getFirst().tags();
        assertEquals(List.of("52"), savedTags);

        assertEquals(1, usersRepositoryORM.findAll().getFirst().links().size());
        assertEquals(1, urlRepositoryORM.findAll().size());
    }
}
