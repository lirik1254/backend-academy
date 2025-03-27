package backend.academy.scrapper.services.ORM;

import static backend.academy.scrapper.utils.ExceptionMessages.CHAT_NOT_FOUND;

import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.entities.JPA.User;
import backend.academy.scrapper.exceptions.ChatNotFoundException;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UsersRepositoryORM;
import backend.academy.scrapper.services.RegistrationService;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
public class RegistrationServiceORM implements RegistrationService {
    private final UsersRepositoryORM usersRepositoryORM;
    private final UrlRepositoryORM urlRepositoryORM;

    @Override
    @Transactional
    public void registerUser(Long chatId) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("access-type", "ORM")
                .setMessage("Регистрация пользователя")
                .log();
        if (!usersRepositoryORM.existsByChatId(chatId)) {
            User user = new User();
            user.chatId(chatId);
            usersRepositoryORM.saveAndFlush(user);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long chatId) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("access-type", "ORM")
                .setMessage("Удаление пользователя")
                .log();
        User user = usersRepositoryORM.findByChatId(chatId);
        if (user == null) {
            throw new ChatNotFoundException(CHAT_NOT_FOUND);
        }

        Set<Url> affectedUrls = user.links().stream().map(Link::url).collect(Collectors.toSet());

        //        user.delete();
        usersRepositoryORM.delete(user);

        affectedUrls.forEach(url -> {
            if (url.links().isEmpty()) {
                urlRepositoryORM.delete(url);
            }
        });
    }
}
