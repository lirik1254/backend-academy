package backend.academy.bot.clients;

import static backend.academy.bot.utils.ClientMessages.TG_CHAT_ID_STRING;
import static general.LogMessages.CHAT_ID_STRING;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class JokeClient {
    private final RestClient restClient;

    public String getJoke(Long chatId) {
        try {
            return restClient
                    .get()
                    .uri("/joke")
                    .header(TG_CHAT_ID_STRING, chatId.toString())
                    .exchange((request, response) -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            log.atInfo()
                                    .addKeyValue(CHAT_ID_STRING, chatId)
                                    .setMessage("Успешно был получен прикол")
                                    .log();
                            return response.bodyTo(String.class);
                        } else {
                            log.atError()
                                    .addKeyValue(CHAT_ID_STRING, chatId)
                                    .setMessage("Не удалось получить прикол")
                                    .log();
                            return "Ошибка генерации анекдота";
                        }
                    });
        } catch (Exception e) {
            return "Ошибка генерации анекдота";
        }
    }
}
