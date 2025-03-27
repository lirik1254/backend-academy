package backend.academy.scrapper.services.SQL;

import static backend.academy.scrapper.utils.ExceptionMessages.LINK_NOT_FOUND;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.entities.SQL.LinkFilters;
import backend.academy.scrapper.entities.SQL.LinkTags;
import backend.academy.scrapper.entities.SQL.Url;
import backend.academy.scrapper.entities.SQL.User;
import backend.academy.scrapper.exceptions.LinkNotFoundException;
import backend.academy.scrapper.repositories.SQL.ContentRepositorySQL;
import backend.academy.scrapper.repositories.SQL.FilterRepositorySQL;
import backend.academy.scrapper.repositories.SQL.LinkRepositorySQL;
import backend.academy.scrapper.repositories.SQL.TagRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UrlRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UsersRepositorySQL;
import backend.academy.scrapper.services.LinkService;
import backend.academy.scrapper.utils.LinkType;
import backend.academy.scrapper.utils.SQL.LinkUtils;
import dto.AddLinkDTO;
import dto.ContentDTO;
import dto.LinkResponseDTO;
import dto.ListLinksResponseDTO;
import general.RegexCheck;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@Slf4j
public class LinkServiceSQL implements LinkService {
    private final UsersRepositorySQL usersRepositorySQL;
    private final LinkRepositorySQL linkRepositorySQL;
    private final FilterRepositorySQL filterRepositorySQL;
    private final TagRepositorySQL tagRepositorySQL;
    private final ContentRepositorySQL contentRepositorySQL;
    private final GitHubInfoClient gitHubInfoClient;
    private final StackOverflowClient stackOverflowClient;
    private final UrlRepositorySQL urlRepositorySQL;
    private final RegexCheck regexCheck;
    private final LinkUtils linkUtils;

    @Override
    @Transactional
    public LinkResponseDTO addLink(Long chatId, AddLinkDTO addRequest) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("link", addRequest.link())
                .addKeyValue("access-type", "SQL")
                .setMessage("Добавление ссылки")
                .log();
        List<String> filters = new ArrayList<>(new HashSet<>(addRequest.filters()));
        List<String> tags = new ArrayList<>(new HashSet<>(addRequest.tags()));

        User user = usersRepositorySQL.getByChatId(chatId);
        if (user == null) {
            usersRepositorySQL.createUser(chatId);
            user = usersRepositorySQL.getByChatId(chatId);
        }

        String link = addRequest.link();
        LinkType linkType = regexCheck.isGithub(link) ? LinkType.GITHUB : LinkType.STACKOVERFLOW;

        Link addLink;
        List<Link> userLink = linkRepositorySQL.getLinksByUrlAndChatId(link, chatId);
        if (!userLink.isEmpty()) {
            addLink = userLink.getFirst();
            Long userId = addLink.userId();
            Long urlId = addLink.urlId();
            filterRepositorySQL.deleteFilters(urlId, userId);
            filters.forEach(filter -> filterRepositorySQL.addFilter(userId, urlId, filter));
            tagRepositorySQL.deleteTags(userId, urlId);
            tags.forEach(tag -> tagRepositorySQL.addTag(userId, urlId, tag));
        } else {
            addLink = urlAndLinkCreate(link, filters, tags, linkType, user.chatId());
        }

        return new LinkResponseDTO(Math.toIntExact(addLink.hashCode()), addRequest.link(), tags, filters);
    }

    private Link urlAndLinkCreate(
            String link, List<String> filters, List<String> tags, LinkType linkType, Long userId) {
        log.atInfo()
                .addKeyValue("link", link)
                .addKeyValue("access-type", "SQL")
                .setMessage("Создание URL")
                .log();
        Url url;
        if (urlRepositorySQL.existUrlByUrl(link)) {
            url = urlRepositorySQL.getByUrl(link);
            linkRepositorySQL.updateLinkUrl(userId, url.id());
        } else {
            urlRepositorySQL.createUrl(link, linkType.toString());
            url = urlRepositorySQL.getByUrl(link);
            List<ContentDTO> contentDTOS;
            if (linkType.equals(LinkType.GITHUB)) {
                contentDTOS = gitHubInfoClient.getGithubContent(link);
            } else {
                contentDTOS = stackOverflowClient.getSOContent(link);
            }
            contentDTOS.forEach(content -> {
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
        Long urlId = url.id();
        linkRepositorySQL.createLink(userId, urlId);
        Link addLink = linkRepositorySQL.getLinksByUrlAndChatId(link, userId).getFirst();
        filters.forEach(filter -> filterRepositorySQL.addFilter(userId, urlId, filter));
        tags.forEach(tag -> tagRepositorySQL.addTag(userId, urlId, tag));

        return addLink;
    }

    @Override
    @Transactional
    public LinkResponseDTO deleteLink(Long chatId, String link) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("link", link)
                .addKeyValue("access-type", "SQL")
                .setMessage("Удаление link")
                .log();
        List<Link> linkToDelete = linkRepositorySQL.getLinksByUrlAndChatId(link, chatId);
        if (!linkToDelete.isEmpty()) {
            Link getLink = linkToDelete.getFirst();
            Long userId = getLink.userId();
            Long urlId = getLink.urlId();

            List<LinkTags> linkTags = tagRepositorySQL.getTagsByUrlIdAndUserId(urlId, userId);
            List<LinkFilters> linkFilters = filterRepositorySQL.getFiltersByUserIdAndUrlId(urlId, userId);

            List<String> tags = linkTags.stream().map(LinkTags::tag).toList();
            List<String> filters = linkFilters.stream().map(LinkFilters::filter).toList();

            linkRepositorySQL.deleteLink(chatId, link);
            urlRepositorySQL.checkDeleteUrl(link);

            return new LinkResponseDTO(Math.toIntExact(getLink.hashCode()), link, tags, filters);
        } else {
            throw new LinkNotFoundException(LINK_NOT_FOUND);
        }
    }

    @Override
    public ListLinksResponseDTO getLinks(Long chatId) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("access-type", "SQL")
                .setMessage("Получение ссылок")
                .log();
        List<Link> userLinks = linkRepositorySQL.getUserLinks(chatId);
        return linkUtils.convertToListLinksResponseDTO(userLinks);
    }
}
