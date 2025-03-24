package backend.academy.scrapper.utils.ORM;

import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.entities.JPA.Content;
import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import dto.ContentDTO;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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

    public List<ContentDTO> fromContentListToContentDTOList(List<Content> contents) {
        List<ContentDTO> contentDTOS = new ArrayList<>();

        contents.forEach(content -> {
            contentDTOS.add(new ContentDTO(
                    content.updatedType(),
                    content.title(),
                    content.userName(),
                    content.creationTime(),
                    content.answer()));
        });

        return contentDTOS;
    }

    @Transactional
    public void updateContentAndSend(Link link, List<ContentDTO> newContent, List<ContentDTO> newItems) {
        Url url = urlRepositoryORM.getUrlByUrl(link.url().url());
        if (!newItems.isEmpty()) {
            url.deleteContent();
            urlRepositoryORM.save(url);
            newContent.forEach(content -> {
                log.atInfo()
                        .addKeyValue("url", url.url())
                        .addKeyValue("access-type", "ORM")
                        .setMessage("Добавление контента в URL")
                        .log();
                Content addContent = new Content();
                addContent.updatedType(content.type());
                addContent.title(content.title());
                addContent.userName(content.userName());
                addContent.answer(content.answer());
                addContent.creationTime(content.creationTime());
                url.addContent(addContent);
            });
        }

        urlRepositoryORM.save(url);

        newItems.forEach(content -> {
            Long chatId = link.users().chatId();
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
