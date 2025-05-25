package backend.academy.scrapper.services.SQL;

import backend.academy.scrapper.entities.SQL.UserSettings;
import backend.academy.scrapper.repositories.SQL.UserSettingsRepositorySQL;
import backend.academy.scrapper.services.interfaces.TimeSettingsService;
import dto.Settings;
import dto.TimeSettingsDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
public class TimeSettingsServiceSQL implements TimeSettingsService {
    private final UserSettingsRepositorySQL userSettingsRepositorySQL;

    @Override
    @Transactional
    public void addTimeSettings(Long chatId, TimeSettingsDTO timeSettingsDTO) {
        userSettingsRepositorySQL.addTimeSettings(chatId, timeSettingsDTO);
    }

    @Override
    public String getTimeSettings(Long chatId) {
        List<UserSettings> userSettings = userSettingsRepositorySQL.getUserSettings(chatId);
        if (userSettings.isEmpty()) {
            return "Сейчас у вас нет настроек";
        } else {
            UserSettings userSetting = userSettings.getFirst();
            if (Objects.equals(userSetting.notifyMood(), Settings.IMMEDIATELY.name())) {
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
