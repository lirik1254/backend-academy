package backend.academy.scrapper.services.SQL;

import static backend.academy.scrapper.utils.ExceptionMessages.LINK_NOT_FOUND;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.entities.SQL.LinkFilters;
import backend.academy.scrapper.entities.SQL.LinkTags;
import backend.academy.scrapper.entities.SQL.Url;
import backend.academy.scrapper.entities.SQL.Users;
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

        Users users = usersRepositorySQL.getByChatId(chatId);
        if (users == null) {
            usersRepositorySQL.createUser(chatId);
            users = usersRepositorySQL.getByChatId(chatId);
        }

        String link = addRequest.link();
        LinkType linkType = regexCheck.isGithub(link) ? LinkType.GITHUB : LinkType.STACKOVERFLOW;

        Link addLink;
        List<Link> userLink = linkRepositorySQL.getLinksByUrlAndChatId(link, chatId);
        if (!userLink.isEmpty()) {
            addLink = userLink.getFirst();
            Long linkId = addLink.linkId();
            filterRepositorySQL.deleteFilter(linkId);
            filters.forEach(filter -> filterRepositorySQL.addFilter(linkId, filter));
            tagRepositorySQL.deleteTags(linkId);
            tags.forEach(tag -> tagRepositorySQL.addTag(linkId, tag));
        } else {
            linkRepositorySQL.createLink(users.usersId());
            addLink = linkRepositorySQL.getLinkByUsersIdAndEmptyUrl(users.usersId());
            filters.forEach(filter -> filterRepositorySQL.addFilter(addLink.linkId(), filter));
            tags.forEach(tag -> tagRepositorySQL.addTag(addLink.linkId(), tag));

            urlCreate(link, addLink, linkType);
        }

        return new LinkResponseDTO(Math.toIntExact(addLink.linkId()), addRequest.link(), tags, filters);
    }

    private void urlCreate(String link, Link addLink, LinkType linkType) {
        log.atInfo()
                .addKeyValue("link", link)
                .addKeyValue("access-type", "SQL")
                .setMessage("Создание URL")
                .log();
        if (urlRepositorySQL.existUrlByUrl(link)) {
            Url url = urlRepositorySQL.getByUrl(link);
            linkRepositorySQL.updateLinkUrl(addLink.linkId(), url.urlId());
        } else {
            urlRepositorySQL.createUrl(link, linkType.toString());
            Url url = urlRepositorySQL.getByUrl(link);

            linkRepositorySQL.updateLinkUrl(addLink.linkId(), url.urlId());
            List<ContentDTO> contentDTOS;
            if (linkType.equals(LinkType.GITHUB)) {
                contentDTOS = gitHubInfoClient.getGithubContent(link);
            } else {
                contentDTOS = stackOverflowClient.getSOContent(link);
            }
            contentDTOS.forEach(content -> {
                contentRepositorySQL.addContent(
                        content.type().name(),
                        content.answer(),
                        content.creationTime(),
                        content.title(),
                        content.userName(),
                        url.urlId());
            });
        }
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
            Long linkId = linkToDelete.getFirst().linkId();
            List<LinkTags> linkTags = tagRepositorySQL.getTagsByLinkId(linkId);
            List<LinkFilters> linkFilters = filterRepositorySQL.getFiltersByLinkId(linkId);

            List<String> tags = linkTags.stream().map(LinkTags::text).toList();
            List<String> filters =
                    linkFilters.stream().map(LinkFilters::filters).toList();

            linkRepositorySQL.deleteLink(chatId, link);
            urlRepositorySQL.checkDeleteUrl(link);

            return new LinkResponseDTO(Math.toIntExact(linkId), link, tags, filters);
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
