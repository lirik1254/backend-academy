package backend.academy.scrapper.utils.SQL;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.entities.SQL.Content;
import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.entities.SQL.Url;
import backend.academy.scrapper.repositories.SQL.ContentRepositorySQL;
import backend.academy.scrapper.repositories.SQL.LinkRepositorySQL;
import backend.academy.scrapper.utils.GetNewItemsUtils;
import backend.academy.scrapper.utils.LinkType;
import dto.ContentDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class UpdateCheckerUtilsSQL {
    private final GitHubInfoClient gitHubInfoClient;
    private final StackOverflowClient stackOverflowClient;
    private final ContentUtilsSQL contentUtilsSQL;
    private final GetNewItemsUtils getNewItemsUtils;
    private final ContentRepositorySQL contentRepositorySQL;
    private final LinkRepositorySQL linkRepositorySQL;

    public void processUrlPage(List<Url> urlPage) {
        urlPage.forEach(url -> {
            List<Content> contentByUrlId = contentRepositorySQL.getContentByUrlId(url.id());
            List<ContentDTO> oldContentDTO = contentUtilsSQL.fromContentListToContentDTOList(contentByUrlId);
            List<ContentDTO> newContent = fetchContent(url);
            List<ContentDTO> newItems = getNewItemsUtils.getNewItems(oldContentDTO, newContent);

            List<Link> linksByUrlId = linkRepositorySQL.getLinksByUrlId(url.id());

            if (!newItems.isEmpty()) {
                linksByUrlId.forEach(link -> {
                    contentUtilsSQL.updateContentAndSend(link, newContent, newItems);
                });
            }
        });
    }

    private List<ContentDTO> fetchContent(Url url) {
        return url.linkType().equals(LinkType.GITHUB.name())
                ? gitHubInfoClient.getGithubContent(url.url())
                : stackOverflowClient.getSOContent(url.url());
    }
}
