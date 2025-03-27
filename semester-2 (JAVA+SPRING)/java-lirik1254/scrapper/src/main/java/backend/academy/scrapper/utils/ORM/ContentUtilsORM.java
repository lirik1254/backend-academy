package backend.academy.scrapper.utils.ORM;

import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.entities.JPA.Content;
import backend.academy.scrapper.entities.JPA.GithubContent;
import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.StackOverflowContent;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.utils.LinkType;
import dto.ContentDTO;
import dto.UpdateType;
import general.RegexCheck;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@Slf4j
public class ContentUtilsORM {
    private final UpdateLinkClient updateLinkClient;
    private final UrlRepositoryORM urlRepositoryORM;
    private final RegexCheck regexCheck;

    public List<ContentDTO> fromContentListToContentDTOList(List<Content> contents) {
        return contents.stream()
                .map(content -> {
                    UpdateType updateType = null;
                    if (content instanceof GithubContent) {
                        updateType = ((GithubContent) content).updatedType();
                    } else if (content instanceof StackOverflowContent) {
                        updateType = ((StackOverflowContent) content).updatedType();
                    }

                    return new ContentDTO(
                            updateType, content.title(), content.userName(), content.creationTime(), content.answer());
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateContentAndSend(Link link, List<ContentDTO> newContent, List<ContentDTO> newItems) {
        LinkType linkType;
        if (regexCheck.isGithub(link.url().url())) {
            linkType = LinkType.GITHUB;
        } else {
            linkType = LinkType.STACKOVERFLOW;
        }
        Url url = urlRepositoryORM.getUrlByUrl(link.url().url());
        if (!newItems.isEmpty()) {
            url.removeContent();
            urlRepositoryORM.save(url);
            newContent.forEach(content -> {
                log.atInfo()
                        .addKeyValue("url", url.url())
                        .addKeyValue("access-type", "ORM")
                        .setMessage("Добавление контента в URL")
                        .log();
                Content.createFromDTO(linkType, content, url);
            });
        }

        urlRepositoryORM.save(url);

        newItems.forEach(content -> {
            Long chatId = link.id().userId();
            String sendUrl = link.url().url();
            log.atInfo()
                    .addKeyValue("chatId", chatId)
                    .addKeyValue("url", sendUrl)
                    .addKeyValue("access-type", "ORM")
                    .setMessage("Отправка контента")
                    .log();
            updateLinkClient.sendUpdate(chatId, sendUrl, content);
        });
    }
}
