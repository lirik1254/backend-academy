package backend.academy.scrapper.utils.SQL;

import backend.academy.scrapper.entities.SQL.Link;
import backend.academy.scrapper.entities.SQL.LinkFilters;
import backend.academy.scrapper.entities.SQL.LinkTags;
import backend.academy.scrapper.repositories.SQL.FilterRepositorySQL;
import backend.academy.scrapper.repositories.SQL.TagRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UrlRepositorySQL;
import dto.LinkResponseDTO;
import dto.ListLinksResponseDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class LinkUtils {
    private final UrlRepositorySQL urlRepositorySQL;
    private final TagRepositorySQL tagRepositorySQL;
    private final FilterRepositorySQL filterRepositorySQL;

    public @NotNull ListLinksResponseDTO convertToListLinksResponseDTO(List<Link> userLinks) {
        List<LinkResponseDTO> linkResponseDTOS = new ArrayList<>();

        userLinks.forEach(link -> {
            Long urlId = link.urlId();
            Long userId = link.userId();
            linkResponseDTOS.add(new LinkResponseDTO(
                    Math.toIntExact(urlId.hashCode() + userId.hashCode()),
                    urlRepositorySQL.getByUrlId(link.urlId()).url(),
                    tagRepositorySQL.getTagsByUrlIdAndUserId(urlId, userId).stream()
                            .map(LinkTags::tag)
                            .toList(),
                    filterRepositorySQL.getFiltersByUserIdAndUrlId(urlId, userId).stream()
                            .map(LinkFilters::filter)
                            .toList()));
        });

        return new ListLinksResponseDTO(linkResponseDTOS, linkResponseDTOS.size());
    }
}
