package backend.academy.scrapper.utils.SQL;

import backend.academy.scrapper.clients.update.UpdateLinkClientFacade;
import backend.academy.scrapper.entities.SQL.Content;
import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.entities.SQL.LinkFilters;
import backend.academy.scrapper.entities.SQL.Url;
import backend.academy.scrapper.entities.SQL.User;
import backend.academy.scrapper.entities.SQL.UserSettings;
import backend.academy.scrapper.repositories.SQL.ContentRepositorySQL;
import backend.academy.scrapper.repositories.SQL.FilterRepositorySQL;
import backend.academy.scrapper.repositories.SQL.LinkRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UrlRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UserSettingsRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UsersRepositorySQL;
import backend.academy.scrapper.services.update.UpdateLinkRedisService;
import backend.academy.scrapper.utils.LinkType;
import dto.ContentDTO;
import dto.Settings;
import dto.UpdateType;
import general.RegexCheck;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@Slf4j
public class ContentUtilsSQL {
    private final UpdateLinkClientFacade updateLinkClientFacade;
    private final UrlRepositorySQL urlRepositorySQL;
    private final ContentRepositorySQL contentRepositorySQL;
    private final UsersRepositorySQL usersRepositorySQL;
    private final RegexCheck regexCheck;
    private final LinkRepositorySQL linkRepositorySQL;
    private final FilterRepositorySQL filterRepositorySQL;
    private final UserSettingsRepositorySQL userSettingsRepositorySQL;
    private final UpdateLinkRedisService updateLinkRedisService;

    public List<ContentDTO> fromContentListToContentDTOList(List<Content> contents) {
        List<ContentDTO> contentDTOS = new ArrayList<>();

        contents.forEach(content -> {
            contentDTOS.add(new ContentDTO(
                    UpdateType.valueOf(content.updatedType()),
                    content.title(),
                    content.userName(),
                    content.creationTime(),
                    content.answer()));
        });
        return contentDTOS;
    }

    @Transactional
    public void updateContentAndSend(Link link, List<ContentDTO> newContent, List<ContentDTO> newItems) {
        LinkType linkType;
        if (regexCheck.isGithub(urlRepositorySQL.getByUrlId(link.urlId()).url())) {
            linkType = LinkType.GITHUB;
        } else {
            linkType = LinkType.STACKOVERFLOW;
        }
        Url url = urlRepositorySQL.getByUrlId(link.urlId());
        if (!newItems.isEmpty()) {
            contentRepositorySQL.deleteContentByUrlId(url.id());
            newContent.forEach(content -> {
                log.atInfo()
                        .addKeyValue("url", url.url())
                        .addKeyValue("access-type", "SQL")
                        .setMessage("Добавление контента в URL")
                        .log();
                contentRepositorySQL.addContent(
                        linkType,
                        content.type().name(),
                        content.answer(),
                        content.creationTime(),
                        content.title(),
                        content.userName(),
                        url.id());
            });
        }

        User user = usersRepositorySQL.getByChatId(link.userId());

        newItems.forEach(content -> {
            Long chatId = user.chatId();
            String sendUrl = url.url();
            List<LinkFilters> filtersByUserIdAndUrlId =
                    filterRepositorySQL.getFiltersByUserIdAndUrlId(url.id(), chatId);
            if (checkUserName(filtersByUserIdAndUrlId, content.userName())) {
                UserSettings userSettings =
                        userSettingsRepositorySQL.getUserSettings(chatId).getFirst();
                if (userSettings.notifyMood().equals(Settings.IMMEDIATELY.name())) {
                    updateLinkClientFacade.sendUpdate(chatId, sendUrl, content);
                    log.atInfo()
                            .addKeyValue("chatId", chatId)
                            .addKeyValue("url", sendUrl)
                            .addKeyValue("access-type", "ORM")
                            .setMessage("Отправка контента")
                            .log();
                } else {
                    updateLinkRedisService.addUpdate(chatId, sendUrl, content);
                }
            }
        });
    }

    private boolean checkUserName(List<LinkFilters> filters, String contentUserName) {
        return filters.stream()
                .map(linkFilter -> linkFilter.filter().split("=")[1])
                .noneMatch(filter -> filter.equals(contentUserName));
    }
}
