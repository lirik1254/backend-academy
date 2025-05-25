package backend.academy.scrapper.services.SQL;

import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.repositories.SQL.LinkRepositorySQL;
import backend.academy.scrapper.services.interfaces.TagLinksService;
import backend.academy.scrapper.utils.SQL.LinkUtils;
import dto.ListLinksResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@Slf4j
public class TagLinksServiceSQL implements TagLinksService {
    private final LinkRepositorySQL linkRepositorySQL;
    private final LinkUtils linkUtils;

    @Override
    public ListLinksResponseDTO getLinks(Long chatId, List<String> tags) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("tags", String.join(" ", tags))
                .addKeyValue("access-type", "SQL")
                .setMessage("Получение ссылок")
                .log();
        List<Link> links = linkRepositorySQL.getLinksByChatIdAndTagsIn(chatId, tags);
        return linkUtils.convertToListLinksResponseDTO(links);
    }
}
