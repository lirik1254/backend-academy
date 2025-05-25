package backend.academy.scrapper.services.SQL;

import static backend.academy.scrapper.utils.ExceptionMessages.CHAT_NOT_FOUND;

import backend.academy.scrapper.exceptions.ChatNotFoundException;
import backend.academy.scrapper.repositories.SQL.UserSettingsRepositorySQL;
import backend.academy.scrapper.repositories.SQL.UsersRepositorySQL;
import backend.academy.scrapper.services.interfaces.RegistrationService;
import dto.Settings;
import dto.TimeSettingsDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class RegistrationServiceSQL implements RegistrationService {
    private final UsersRepositorySQL usersRepositorySQL;
    private final UserSettingsRepositorySQL userSettingsRepositorySQL;

    @Override
    @Transactional
    public void registerUser(Long chatId) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("access-type", "SQL")
                .setMessage("Регистрация пользователя")
                .log();
        usersRepositorySQL.createUser(chatId);
        userSettingsRepositorySQL.addTimeSettings(chatId, new TimeSettingsDTO(Settings.IMMEDIATELY, null));
    }

    @Override
    @Transactional
    public void deleteUser(Long chatId) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("access-type", "SQL")
                .setMessage("Удаление пользователя")
                .log();
        if (usersRepositorySQL.getByChatId(chatId) == null) {
            throw new ChatNotFoundException(CHAT_NOT_FOUND);
        }
        usersRepositorySQL.deleteUser(chatId);
    }
}
