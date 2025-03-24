package backend.academy.scrapper.link.SQL;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.link.DeleteLinkTestsBase;
import backend.academy.scrapper.repositories.SQL.ContentRepositorySQL;
import backend.academy.scrapper.repositories.SQL.FilterRepositorySQL;
import backend.academy.scrapper.repositories.SQL.LinkRepositorySQL;
import backend.academy.scrapper.repositories.SQL.TagRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UrlRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UsersRepositorySQL;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.RemoveLinkRequest;
import dto.UpdateType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class DeleteLinkTestsSQL extends DeleteLinkTestsBase {

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
    @DisplayName("Удаление link")
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

        assertFalse(usersRepositorySQL.findAll().isEmpty());
        assertFalse(linkRepositorySQL.findAll().isEmpty());
        assertFalse(urlRepositorySQL.findAllWithPagination(0, 10).get().toList().isEmpty());
        assertFalse(contentRepositorySQL.findAll().isEmpty());
        assertFalse(filterRepositorySQL.findAll().isEmpty());
        assertFalse(tagRepositorySQL.findAll().isEmpty());

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(githubLink);

        performDeleteLinkRequest(removeLinkRequest, 123L, githubLink, tags, filters);

        assertTrue(linkRepositorySQL.findAll().isEmpty());
        assertTrue(urlRepositorySQL.findAllWithPagination(0, 10).get().toList().isEmpty());
        assertTrue(contentRepositorySQL.findAll().isEmpty());
        assertTrue(filterRepositorySQL.findAll().isEmpty());
        assertTrue(tagRepositorySQL.findAll().isEmpty());
    }

    @Test
    @DisplayName("Удаление несуществующей ссылки")
    public void test2() throws Exception {
        assertTrue(linkRepositorySQL.findAll().isEmpty());

        String stackOverflowlink = "https://stackoverflow.com/questions/34534534";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(stackOverflowlink);

        performDeleteLinkRequestNotFound(removeLinkRequest, 123L);
    }
}
