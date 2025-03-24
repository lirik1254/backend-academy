package backend.academy.scrapper.utils.ORM;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.utils.GetNewItemsUtils;
import backend.academy.scrapper.utils.LinkType;
import dto.ContentDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public class UpdateCheckerUtilsORM {
    private final ContentUtilsORM contentUtilsORM;
    private final GetNewItemsUtils getNewItemsUtils;
    private final GitHubInfoClient gitHubInfoClient;
    private final StackOverflowClient stackOverflowClient;

    public void processUrlPage(List<Url> urlPage) {
        urlPage.forEach(url -> {
            List<ContentDTO> oldContentDTO = contentUtilsORM.fromContentListToContentDTOList(url.contents());
            List<ContentDTO> newContent = fetchContent(url);
            List<ContentDTO> newItems = getNewItemsUtils.getNewItems(oldContentDTO, newContent);

            if (!newItems.isEmpty()) {
                url.links().forEach(link -> {
                    contentUtilsORM.updateContentAndSend(link, newContent, newItems);
                });
            }
        });
    }

    private List<ContentDTO> fetchContent(Url url) {
        return url.linkType() == LinkType.GITHUB
                ? gitHubInfoClient.getGithubContent(url.url())
                : stackOverflowClient.getSOContent(url.url());
    }
}
