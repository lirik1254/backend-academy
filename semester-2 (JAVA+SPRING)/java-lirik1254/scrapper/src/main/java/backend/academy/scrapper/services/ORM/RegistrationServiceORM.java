package backend.academy.scrapper.services.ORM;

import static backend.academy.scrapper.utils.ExceptionMessages.CHAT_NOT_FOUND;

import backend.academy.scrapper.entities.JPA.Link;
import backend.academy.scrapper.entities.JPA.Url;
import backend.academy.scrapper.entities.JPA.User;
import backend.academy.scrapper.entities.JPA.UserSettings;
import backend.academy.scrapper.exceptions.ChatNotFoundException;
import backend.academy.scrapper.repositories.ORM.UrlRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UserSettingsRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UsersRepositoryORM;
import backend.academy.scrapper.services.interfaces.RegistrationService;
import dto.Settings;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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
    private final UserSettingsRepositoryORM userSettingsRepositoryORM;

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

            UserSettings userSettings = new UserSettings();
            userSettings.notifyTime(null);
            userSettings.notifyMood(Settings.IMMEDIATELY);

            user.userSettings(userSettings);
            userSettings.user(user);

            userSettingsRepositoryORM.save(userSettings);
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

        List<Link> linksToDelete = new ArrayList<>(user.links());

        linksToDelete.forEach(link -> {
            link.tags().clear();
            link.filters().clear();

            Url url = link.url();
            if (url != null) {
                url.links().remove(link);
                if (url.links().isEmpty()) {
                    url.contents().clear();
                    urlRepositoryORM.delete(url);
                }
            }
        });

        usersRepositoryORM.delete(user);
    }
}
