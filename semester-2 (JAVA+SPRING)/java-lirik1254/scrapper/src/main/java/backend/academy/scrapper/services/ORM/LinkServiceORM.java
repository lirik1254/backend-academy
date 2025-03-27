package backend.academy.scrapper.services.ORM;

import static backend.academy.scrapper.utils.ExceptionMessages.LINK_NOT_FOUND;

import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.entities.JPA.Content;
import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.LinkId;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.entities.JPA.User;
import backend.academy.scrapper.exceptions.LinkNotFoundException;
import backend.academy.scrapper.repositories.ORM.ContentRepositoryORM;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private final ContentRepositoryORM contentRepositoryORM;

    @PersistenceContext
    private EntityManager entityManager;

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
        User user = usersRepositoryORM.getByChatId(chatId);
        if (user == null) {
            registrationServiceORM.registerUser(chatId);
            user = usersRepositoryORM.getByChatId(chatId);
        }

        String link = addRequest.link();
        LinkType linkType = regexCheck.isGithub(link) ? LinkType.GITHUB : LinkType.STACKOVERFLOW;

        Link addLink;
        if (!user.links().stream()
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

            urlCreate(link, addLink, linkType, user, addRequest);
        }

        return new LinkResponseDTO(
                Math.toIntExact(addLink.id().hashCode()), addLink.url().url(), addLink.tags(), addLink.filters());
    }

    private void urlCreate(String link, Link addLink, LinkType linkType, User user, AddLinkDTO addLinkDTO) {
        log.atInfo()
                .addKeyValue("link", link)
                .addKeyValue("access-type", "ORM")
                .setMessage("Создание URL")
                .log();
        Url url;
        if (urlRepositoryORM.existsUrlByUrl(link)) {
            url = urlRepositoryORM.getUrlByUrl(link);
        } else {
            url = new Url();
            url.url(link);
            url.linkType(linkType);
            urlRepositoryORM.saveAndFlush(url);
            List<ContentDTO> contentDTOs;
            if (linkType.equals(LinkType.GITHUB)) {
                contentDTOs = gitHubInfoClient.getGithubContent(link);
            } else {
                contentDTOs = stackOverflowClient.getSOContent(link);
            }
            contentDTOs.forEach(contentDTO -> {
                Content createContent = Content.createFromDTO(linkType, contentDTO, url);
                contentRepositoryORM.save(createContent);
            });
        }
        addLink.setUrl(url);
        addLink.setUser(user);
        LinkId linkId = new LinkId();
        linkId.userId(user.chatId());
        linkId.urlId(url.id());
        addLink.id(linkId);
        entityManager.clear();
        linkRepositoryORM.save(addLink);
        addLink.filters(addLinkDTO.filters());
        addLink.tags(addLinkDTO.tags());
        linkRepositoryORM.save(addLink);
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
            User user = usersRepositoryORM.findByChatId(chatId);
            Url url = urlRepositoryORM.getUrlByUrl(link);

            Link getLinkToDelete = linkToDelete.getFirst();
            getLinkToDelete.deleteLink();

            usersRepositoryORM.save(user);
            urlRepositoryORM.save(url);

            if (url.links().isEmpty()) {
                urlRepositoryORM.delete(url);
            }

            return new LinkResponseDTO(
                    Math.toIntExact(getLinkToDelete.id().hashCode()),
                    link,
                    getLinkToDelete.tags(),
                    getLinkToDelete.filters());
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
        List<Link> links = linkRepositoryORM.findByUser_ChatId(chatId);
        List<LinkResponseDTO> linkResponseDTOS = new ArrayList<>();

        links.forEach(link -> linkResponseDTOS.add(new LinkResponseDTO(
                Math.toIntExact(link.id().hashCode()), link.url().url(), link.tags(), link.filters())));

        return new ListLinksResponseDTO(linkResponseDTOS, linkResponseDTOS.size());
    }
}
