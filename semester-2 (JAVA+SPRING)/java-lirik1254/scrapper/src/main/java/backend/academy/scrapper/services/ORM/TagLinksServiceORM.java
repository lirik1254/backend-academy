package backend.academy.scrapper.services.ORM;

import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.repositories.ORM.LinkRepositoryORM;
import backend.academy.scrapper.services.TagLinksService;
import dto.LinkResponseDTO;
import dto.ListLinksResponseDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@Slf4j
public class TagLinksServiceORM implements TagLinksService {
    private final LinkRepositoryORM linkRepositoryORM;

    @Override
    public ListLinksResponseDTO getLinks(Long chatId, List<String> tags) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("tags", String.join(" ", tags))
                .addKeyValue("access-type", "ORM")
                .setMessage("Получение ссылок")
                .log();
        List<Link> retLinks = linkRepositoryORM.findByUsers_ChatIdAndTagsIn(chatId, tags);

        List<LinkResponseDTO> linkResponseDTOS = new ArrayList<>();

        retLinks.forEach(link -> linkResponseDTOS.add(
                new LinkResponseDTO(Math.toIntExact(link.linkId()), link.url().url(), link.tags(), link.filters())));

        return new ListLinksResponseDTO(linkResponseDTOS, linkResponseDTOS.size());
    }
}
