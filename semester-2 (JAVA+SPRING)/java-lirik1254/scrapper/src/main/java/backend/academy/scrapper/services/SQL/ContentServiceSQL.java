package backend.academy.scrapper.services.SQL;

import backend.academy.scrapper.entities.SQL.Content;
import backend.academy.scrapper.repositories.SQL.ContentRepositorySQL;
import backend.academy.scrapper.services.interfaces.ContentService;
import dto.ContentDTO;
import dto.UpdateType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@Slf4j
public class ContentServiceSQL implements ContentService {
    private final ContentRepositorySQL contentRepositorySQL;

    @Override
    public List<ContentDTO> getContentByRandomUrl(Long chatId) {
        List<Content> content = contentRepositorySQL.getContentByRandomUrl(chatId);

        return content.stream()
                .map(getContent -> new ContentDTO(
                        UpdateType.valueOf(getContent.updatedType()),
                        getContent.title(),
                        getContent.userName(),
                        getContent.creationTime(),
                        getContent.answer()))
                .toList();
    }
}
