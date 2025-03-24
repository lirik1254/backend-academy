package backend.academy.scrapper.link.ORM;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.link.DeleteLinkTestsBase;
import backend.academy.scrapper.repositories.ORM.ContentRepositoryORM;
import backend.academy.scrapper.repositories.ORM.LinkRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UsersRepositoryORM;
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

public class DeleteLinkTestsORM extends DeleteLinkTestsBase {

    @Autowired
    LinkRepositoryORM linkRepositoryORM;

    @Autowired
    ContentRepositoryORM contentRepositoryORM;

    @Autowired
    UrlRepositoryORM urlRepositoryORM;

    @Autowired
    UsersRepositoryORM usersRepositoryORM;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.access-type", () -> "ORM");
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

        assertFalse(usersRepositoryORM.findAll().isEmpty());
        assertFalse(linkRepositoryORM.findAll().isEmpty());
        assertFalse(urlRepositoryORM.findAll().isEmpty());
        assertFalse(contentRepositoryORM.findAll().isEmpty());

        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(githubLink);

        performDeleteLinkRequest(removeLinkRequest, 123L, githubLink, tags, filters);

        assertTrue(linkRepositoryORM.findAll().isEmpty());
        assertTrue(urlRepositoryORM.findAll().isEmpty());
        assertTrue(contentRepositoryORM.findAll().isEmpty());
    }

    @Test
    @DisplayName("Удаление несуществующей ссылки")
    public void test2() throws Exception {
        assertTrue(linkRepositoryORM.findAll().isEmpty());

        String stackOverflowlink = "https://stackoverflow.com/questions/34534534";
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(stackOverflowlink);

        performDeleteLinkRequestNotFound(removeLinkRequest, 123L);
    }
}
