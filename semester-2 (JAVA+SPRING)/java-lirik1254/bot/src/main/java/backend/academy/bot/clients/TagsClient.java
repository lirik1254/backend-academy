package backend.academy.bot.clients;

import static general.LogMessages.CHAT_ID_STRING;
import static general.LogMessages.STATUS;

import backend.academy.bot.BotConfig;
import dto.ApiErrorResponseDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class TagsClient {

    private final RestClient restClient;
    private final BotConfig botConfig;

    public TagsClient(BotConfig botConfig, RestClient restClient) {
        this.botConfig = botConfig;
        this.restClient = restClient;
    }

    public String getAllTags(Long chatId) {
        try {
            return restClient.get().uri("/tags/{id}", chatId).exchange((request, response) -> {
                if (response.getStatusCode().is2xxSuccessful()) {
                    List<String> tags = response.bodyTo(List.class);
                    if (tags.isEmpty()) {
                        return "У вас пока нет тегов";
                    } else {
                        return String.join("\n", tags);
                    }
                } else {
                    ApiErrorResponseDTO error = response.bodyTo(ApiErrorResponseDTO.class);
                    if (error != null && error.description() != null) {
                        log.atError()
                                .addKeyValue(CHAT_ID_STRING, chatId)
                                .addKeyValue(STATUS, response.getStatusCode())
                                .addKeyValue("description", error.description())
                                .setMessage("Не удалось получить теги")
                                .log();
                        return error.description();
                    } else {
                        log.atError()
                                .addKeyValue(CHAT_ID_STRING, chatId)
                                .addKeyValue(STATUS, response.getStatusCode())
                                .setMessage("Не удалось получить теги - Не удалось прочитать тело ответа")
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
