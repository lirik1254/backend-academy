package backend.academy.bot.clients;

import static general.LogMessages.CHAT_ID_STRING;

import backend.academy.bot.BotConfig;
import backend.academy.bot.utils.UnsuccessfulResponseUtils;
import dto.ListLinksResponseDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class TagLinksClient {

    private final RestClient restClient;
    private final BotConfig botConfig;
    private final UnsuccessfulResponseUtils unsuccessfulResponseUtils;

    public TagLinksClient(BotConfig botConfig, UnsuccessfulResponseUtils unsuccessfulResponseUtils) {
        this.botConfig = botConfig;
        this.restClient = RestClient.create(botConfig.baseUrl());
        this.unsuccessfulResponseUtils = unsuccessfulResponseUtils;
    }

    public String getAllTagLinks(Long chatId, List<String> tags) {
        try {
            return restClient
                    .post()
                    .uri("/link-tags/{chatId}", chatId)
                    .body(tags)
                    .exchange((request, response) -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            ListLinksResponseDTO dto = response.bodyTo(ListLinksResponseDTO.class);
                            if (dto != null) {
                                String result = dto.toString();
                                log.atInfo()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .addKeyValue("links", result)
                                        .setMessage("Успешный ответ от /linktags")
                                        .log();
                                return result.equals("Список отслеживаемых ссылок пуст!")
                                        ? "Нет ссылок по предложенным тегам"
                                        : result;
                            } else {
                                log.atWarn()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .setMessage("Успешный ответ от /linktags, но тело пустое или невалидное")
                                        .log();
                                return "Не удалось получить список отслеживаемых ссылок";
                            }
                        } else {
                            return unsuccessfulResponseUtils.unsuccessfulResponse(chatId, response);
                        }
                    });
        } catch (Exception e) {
            return "Ошибка";
        }
    }
}
