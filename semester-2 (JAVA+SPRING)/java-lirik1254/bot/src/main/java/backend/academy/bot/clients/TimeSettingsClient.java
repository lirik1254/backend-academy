package backend.academy.bot.clients;

import static backend.academy.bot.utils.ClientMessages.ERROR;
import static backend.academy.bot.utils.ClientMessages.TG_CHAT_ID_STRING;
import static general.LogMessages.CHAT_ID_STRING;

import dto.ApiErrorResponseDTO;
import dto.TimeSettingsDTO;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimeSettingsClient {
    private final RestClient restClient;

    public String saveTimeSettings(Long chatId, TimeSettingsDTO timeSettingsDTO) {
        try {
            return restClient
                    .post()
                    .uri("/time")
                    .header(TG_CHAT_ID_STRING, chatId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(timeSettingsDTO)
                    .exchange((request, response) -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            log.atInfo()
                                    .addKeyValue(CHAT_ID_STRING, chatId)
                                    .setMessage("Успешно сохранились настройки отправки")
                                    .log();
                            return "Сохранено";
                        } else {
                            log.atError()
                                    .addKeyValue(CHAT_ID_STRING, chatId)
                                    .setMessage("Не удалось сохранить настройки отправки")
                                    .log();
                            return Optional.ofNullable(response.bodyTo(ApiErrorResponseDTO.class))
                                    .map(ApiErrorResponseDTO::description)
                                    .orElse(ERROR);
                        }
                    });
        } catch (Exception e) {
            return "Ошибка";
        }
    }

    public String getTimeSettings(Long chatId) {
        try {
            return restClient
                    .get()
                    .uri("/time")
                    .header(TG_CHAT_ID_STRING, chatId.toString())
                    .exchange((request, response) -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            log.atInfo()
                                    .addKeyValue(CHAT_ID_STRING, chatId)
                                    .setMessage("Успешно были получены настройки отправки у пользователя")
                                    .log();
                            return response.bodyTo(String.class);
                        } else {
                            log.atError()
                                    .addKeyValue(CHAT_ID_STRING, chatId)
                                    .setMessage("Не удалось получить настройки отправки у пользователя")
                                    .log();
                            return "Ошибка";
                        }
                    });
        } catch (Exception e) {
            return "Ошибка";
        }
    }
}
