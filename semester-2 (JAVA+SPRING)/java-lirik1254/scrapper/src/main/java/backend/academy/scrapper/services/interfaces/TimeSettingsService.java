package backend.academy.scrapper.services.interfaces;

import dto.TimeSettingsDTO;

public interface TimeSettingsService {
    void addTimeSettings(Long chatId, TimeSettingsDTO timeSettingsDTO);

    String getTimeSettings(Long chatId);
}
