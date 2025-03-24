package backend.academy.scrapper.services.ORM;

import static backend.academy.scrapper.utils.ExceptionMessages.LINK_NOT_FOUND;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.entities.JPA.Content;
import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.entities.JPA.Users;
import backend.academy.scrapper.exceptions.LinkNotFoundException;
import backend.academy.scrapper.repositories.ORM.LinkRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UsersRepositoryORM;
import backend.academy.scrapper.services.LinkService;
import backend.academy.scrapper.utils.LinkType;
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
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@Slf4j
public class LinkServiceORM implements LinkService {
    private final LinkRepositoryORM linkRepositoryORM;
    private final UsersRepositoryORM usersRepositoryORM;
    private final RegexCheck regexCheck;
    private final GitHubInfoClient gitHubInfoClient;
    private final StackOverflowClient stackOverflowClient;
    private final UrlRepositoryORM urlRepositoryORM;
    private final RegistrationServiceORM registrationServiceORM;

    @Override
    @Transactional
    public LinkResponseDTO addLink(Long chatId, AddLinkDTO addRequest) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("link", addRequest.link())
                .addKeyValue("access-type", "ORM")
                .setMessage("Добавление ссылки")
                .log();
        List<String> filters = new ArrayList<>(new HashSet<>(addRequest.filters()));
        List<String> tags = new ArrayList<>(new HashSet<>(addRequest.tags()));
        Users users = usersRepositoryORM.getByChatId(chatId);
        if (users == null) {
            registrationServiceORM.registerUser(chatId);
            users = usersRepositoryORM.getByChatId(chatId);
        }

        String link = addRequest.link();
        LinkType linkType = regexCheck.isGithub(link) ? LinkType.GITHUB : LinkType.STACKOVERFLOW;

        Link addLink;
        if (!users.links().stream()
                .filter(user_link -> user_link.url().url().equals(link))
                .toList()
                .isEmpty()) {
            List<Link> existingLinks = linkRepositoryORM.findByUrlAndChatId(link, chatId);
            addLink = existingLinks.getFirst();
            addLink.filters(filters);
            addLink.tags(tags);
            linkRepositoryORM.save(addLink);
        } else {
            addLink = new Link();
            addLink.filters(filters);
            addLink.tags(tags);

            urlCreate(link, addLink, linkType);

            users.addLink(addLink);
            usersRepositoryORM.save(users);
        }

        return new LinkResponseDTO(
                Math.toIntExact(addLink.linkId()), addLink.url().url(), addLink.tags(), addLink.filters());
    }

    private void urlCreate(String link, Link addLink, LinkType linkType) {
        log.atInfo()
                .addKeyValue("link", link)
                .addKeyValue("access-type", "ORM")
                .setMessage("Создание URL")
                .log();
        Url url;
        if (urlRepositoryORM.existsUrlByUrl(link)) {
            url = urlRepositoryORM.getUrlByUrl(link);
            url.addLink(addLink);
            linkRepositoryORM.save(addLink);
        } else {
            url = new Url();
            url.url(link);
            url.linkType(linkType);
            List<ContentDTO> contentDTOs;
            if (linkType.equals(LinkType.GITHUB)) {
                contentDTOs = gitHubInfoClient.getGithubContent(link);
            } else {
                contentDTOs = stackOverflowClient.getSOContent(link);
            }
            contentDTOs.forEach(content -> {
                Content addContent = new Content();
                addContent.updatedType(content.type());
                addContent.answer(content.answer());
                addContent.creationTime(content.creationTime());
                addContent.title(content.title());
                addContent.userName(content.userName());

                url.addContent(addContent);
            });
            url.addLink(addLink);
            urlRepositoryORM.save(url);
        }
    }

    @Override
    @Transactional
    public LinkResponseDTO deleteLink(Long chatId, String link) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("link", link)
                .addKeyValue("access-type", "ORM")
                .setMessage("Удаление link")
                .log();
        List<Link> linkToDelete = linkRepositoryORM.findByUrlAndChatId(link, chatId);
        if (!linkToDelete.isEmpty()) {
            Users users = usersRepositoryORM.findByChatId(chatId);
            Url url = urlRepositoryORM.getUrlByUrl(link);

            Link getLinkToDelete = linkToDelete.getFirst();
            getLinkToDelete.deleteLink();

            usersRepositoryORM.save(users);
            urlRepositoryORM.save(url);

            if (url.links().isEmpty()) {
                urlRepositoryORM.delete(url);
            }

            return new LinkResponseDTO(
                    Math.toIntExact(getLinkToDelete.linkId()), link, getLinkToDelete.tags(), getLinkToDelete.filters());
        } else {
            throw new LinkNotFoundException(LINK_NOT_FOUND);
        }
    }

    @Override
    public ListLinksResponseDTO getLinks(Long chatId) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("access-type", "ORM")
                .setMessage("Получение ссылок")
                .log();
        List<Link> links = linkRepositoryORM.findByUsers_ChatId(chatId);
        List<LinkResponseDTO> linkResponseDTOS = new ArrayList<>();

        links.forEach(link -> linkResponseDTOS.add(
                new LinkResponseDTO(Math.toIntExact(link.linkId()), link.url().url(), link.tags(), link.filters())));

        return new ListLinksResponseDTO(linkResponseDTOS, linkResponseDTOS.size());
    }
}
