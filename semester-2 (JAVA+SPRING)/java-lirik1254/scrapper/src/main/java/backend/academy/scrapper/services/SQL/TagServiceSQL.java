package backend.academy.scrapper.services.SQL;

import backend.academy.scrapper.repositories.SQL.TagRepositorySQL;
import backend.academy.scrapper.services.interfaces.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@Slf4j
public class TagServiceSQL implements TagService {
    private final TagRepositorySQL tagRepositorySQL;

    @Override
    public List<String> getAllTags(Long chatId) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("access-type", "SQL")
                .setMessage("Получение всех тегов")
                .log();
        return tagRepositorySQL.getAllTagsByUserId(chatId);
    }
}
