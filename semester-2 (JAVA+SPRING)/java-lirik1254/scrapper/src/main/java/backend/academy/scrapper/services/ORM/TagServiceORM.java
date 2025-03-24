package backend.academy.scrapper.services.ORM;

import backend.academy.scrapper.repositories.ORM.LinkRepositoryORM;
import backend.academy.scrapper.services.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@Slf4j
public class TagServiceORM implements TagService {
    private final LinkRepositoryORM linkRepositoryORM;

    @Override
    public List<String> getAllTags(Long chatId) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("access-type", "ORM")
                .setMessage("Получение всех тегов")
                .log();
        return linkRepositoryORM.getTagsByUsers_ChatId(chatId);
    }
}
