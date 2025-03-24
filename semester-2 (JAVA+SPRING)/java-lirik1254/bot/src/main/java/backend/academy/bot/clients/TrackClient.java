package backend.academy.bot.clients;

import static general.LogMessages.CHAT_ID_STRING;
import static general.LogMessages.LINK_STRING;
import static general.LogMessages.STATUS;

import backend.academy.bot.BotConfig;
import backend.academy.bot.utils.UnsuccessfulResponseUtils;
import dto.AddLinkDTO;
import dto.ApiErrorResponseDTO;
import dto.ListLinksResponseDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrackClient {
    public static final String ERROR = "Ошибка";
    public static final String TG_CHAT_ID_STRING = "Tg-Chat-Id";
    public static final String LINKS_COMMAND_STRING = "/links";
    public static final String GITHUB_COM_STRING = "github.com";

    private final RestClient restClient;
    private final BotConfig botConfig;
    private final UnsuccessfulResponseUtils unsuccessfulResponseUtils;

    public String trackLink(Long chatId, String link, List<String> tags, List<String> filters) {
        try {
            return restClient
                    .post()
                    .uri(LINKS_COMMAND_STRING)
                    .header(TG_CHAT_ID_STRING, chatId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AddLinkDTO(link, tags, filters))
                    .exchange((request, response) -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            if (link.contains(GITHUB_COM_STRING)) {
                                log.atInfo()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .addKeyValue(LINK_STRING, link)
                                        .setMessage("Изменения в репозитории у чата теперь отслеживаются")
                                        .log();
                                return "Изменения в репозитории теперь отслеживаются";
                            } else {
                                log.atInfo()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .addKeyValue(LINK_STRING, link)
                                        .setMessage("Новые ответы на вопрос у чата теперь отслеживаются")
                                        .log();
                                return "Новые ответы на вопрос теперь отслеживаются";
                            }
                        } else {
                            log.atError()
                                    .addKeyValue(LINK_STRING, link)
                                    .addKeyValue(CHAT_ID_STRING, chatId)
                                    .setMessage("Произошла ошибка при отслеживании ссылки")
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

    public String unTrackLink(Long chatId, String link) {
        try {
            return restClient
                    .method(HttpMethod.DELETE)
                    .uri(LINKS_COMMAND_STRING)
                    .header(TG_CHAT_ID_STRING, chatId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(LINK_STRING, link))
                    .exchange((request, response) -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            if (link.contains(GITHUB_COM_STRING)) {
                                log.atInfo()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .addKeyValue(LINK_STRING, link)
                                        .setMessage("Изменения в репозитории у чата теперь больше не отслеживаются")
                                        .log();
                                return "Изменения в репозитории больше не отслеживается";
                            } else {
                                log.atInfo()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .addKeyValue(LINK_STRING, link)
                                        .setMessage("Изменения в вопросе у чата больше не отслеживаются")
                                        .log();
                                return "Изменения в вопросе больше не отслеживаются";
                            }
                        } else {
                            ApiErrorResponseDTO error = response.bodyTo(ApiErrorResponseDTO.class);
                            if (error != null) {
                                log.atError()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .addKeyValue(LINK_STRING, link)
                                        .addKeyValue(STATUS, response.getStatusCode())
                                        .addKeyValue("error", error.description())
                                        .setMessage("Не удалось прекратить отслеживание ссылки")
                                        .log();
                                return error.description();
                            } else {
                                log.atError()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .addKeyValue(LINK_STRING, link)
                                        .addKeyValue(STATUS, response.getStatusCode())
                                        .setMessage(
                                                "Не удалось прекратить отслеживание ссылки: Тело ответа не удалось прочитать")
                                        .log();
                                return ERROR;
                            }
                        }
                    });
        } catch (Exception e) {
            return "Ошибка";
        }
    }

    public String getTrackLinks(Long chatId) {
        try {
            return restClient
                    .get()
                    .uri(LINKS_COMMAND_STRING)
                    .header(TG_CHAT_ID_STRING, String.valueOf(chatId))
                    .exchange((request, response) -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            ListLinksResponseDTO dto = response.bodyTo(ListLinksResponseDTO.class);
                            if (dto != null) {
                                String result = dto.toString();
                                log.atInfo()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .addKeyValue("links", result)
                                        .setMessage("Успешный ответ от /links")
                                        .log();
                                return result;
                            } else {
                                log.atWarn()
                                        .addKeyValue(CHAT_ID_STRING, chatId)
                                        .setMessage("Успешный ответ от /links, но тело пустое или невалидное")
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
