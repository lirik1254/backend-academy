package backend.academy.bot.clients;

import static general.LogMessages.CHAT_ID_STRING;
import static general.LogMessages.STATUS;

import backend.academy.bot.BotConfig;
import dto.ApiErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class RegistrationClient {
    public static final String CHAT_REGISTERED = "Чат успешно зарегистрирован";

    private final RestClient restClient;
    private final BotConfig botConfig;

    public RegistrationClient(BotConfig botConfig, RestClient restClient) {
        this.botConfig = botConfig;
        this.restClient = restClient;
    }

    public String registerUser(Long chatId) {
        try {
            return restClient.post().uri("/tg-chat/{id}", chatId).exchange((request, response) -> {
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.atInfo()
                            .addKeyValue(CHAT_ID_STRING, chatId)
                            .setMessage(CHAT_REGISTERED)
                            .log();
                    return CHAT_REGISTERED;
                } else {
                    ApiErrorResponseDTO error = response.bodyTo(ApiErrorResponseDTO.class);
                    if (error != null) {
                        log.atError()
                                .addKeyValue(CHAT_ID_STRING, chatId)
                                .addKeyValue(STATUS, response.getStatusCode())
                                .addKeyValue("description", error.description())
                                .setMessage("Не удалось зарегистрировать чат")
                                .log();
                        return error.description();
                    } else {
                        log.atError()
                                .addKeyValue(CHAT_ID_STRING, chatId)
                                .addKeyValue(STATUS, response.getStatusCode())
                                .setMessage("Не удалось зарегистрировать чат - Не удалось прочитать тело ответа")
                                .log();
                        return "Ошибка";
                    }
                }
            });
        } catch (Exception e) {
            return "Ошибка";
        }
    }
}
