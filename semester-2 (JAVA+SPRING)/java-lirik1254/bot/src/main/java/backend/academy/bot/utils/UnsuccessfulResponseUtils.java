package backend.academy.bot.utils;

import static general.LogMessages.CHAT_ID_STRING;
import static general.LogMessages.STATUS;

import dto.ApiErrorResponseDTO;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class UnsuccessfulResponseUtils {
    public String unsuccessfulResponse(
            Long chatId, RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) throws IOException {
        ApiErrorResponseDTO error = response.bodyTo(ApiErrorResponseDTO.class);
        if (error != null) {
            log.atError()
                    .addKeyValue(CHAT_ID_STRING, chatId)
                    .addKeyValue(STATUS, response.getStatusCode())
                    .addKeyValue("description", error.description())
                    .setMessage("Ошибка при запросе /links")
                    .log();
            return error.description();
        } else {
            log.atError()
                    .addKeyValue(CHAT_ID_STRING, chatId)
                    .addKeyValue(STATUS, response.getStatusCode())
                    .setMessage("Ошибка при запросе /links, тело не удалось прочитать")
                    .log();
            return "Произошла ошибка";
        }
    }
}
