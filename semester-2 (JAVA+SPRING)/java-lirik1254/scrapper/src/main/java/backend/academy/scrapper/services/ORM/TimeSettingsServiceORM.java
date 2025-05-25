package backend.academy.scrapper.services.ORM;

import backend.academy.scrapper.entities.JPA.User;
import backend.academy.scrapper.entities.JPA.UserSettings;
import backend.academy.scrapper.repositories.ORM.UserSettingsRepositoryORM;
import backend.academy.scrapper.repositories.ORM.UsersRepositoryORM;
import backend.academy.scrapper.services.interfaces.TimeSettingsService;
import dto.Settings;
import dto.TimeSettingsDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@Slf4j
public class TimeSettingsServiceORM implements TimeSettingsService {
    private final UserSettingsRepositoryORM userSettingsRepositoryORM;
    private final UsersRepositoryORM usersRepositoryORM;

    @Override
    @Transactional
    public void addTimeSettings(Long chatId, TimeSettingsDTO timeSettingsDTO) {
        List<UserSettings> setting = userSettingsRepositoryORM.findByUserId(chatId);
        if (setting == null || setting.isEmpty()) {
            UserSettings userSettings = new UserSettings();
            User user = usersRepositoryORM.findByChatId(chatId);

            userSettings.user(user);
            userSettings.notifyTime(timeSettingsDTO.notifyTime());
            userSettings.notifyMood(timeSettingsDTO.notifyMood());
            userSettingsRepositoryORM.save(userSettings);
        } else {
            UserSettings userSetting = setting.getFirst();
            userSetting.notifyMood(timeSettingsDTO.notifyMood());
            userSetting.notifyTime(timeSettingsDTO.notifyTime());
        }
    }

    @Override
    public String getTimeSettings(Long chatId) {
        List<UserSettings> userSettings = userSettingsRepositoryORM.findByUserId(chatId);
        if (userSettings.isEmpty()) {
            return "Сейчас у вас нет настроек";
        } else {
            UserSettings userSetting = userSettings.getFirst();
            if (userSetting.notifyMood() == Settings.IMMEDIATELY) {
                return """
                    Отправлять сразу""";
            } else {
                String time = userSetting.notifyTime().toString();
                return String.format("""
                    Отправлять к %s""", time);
            }
        }
    }
}
