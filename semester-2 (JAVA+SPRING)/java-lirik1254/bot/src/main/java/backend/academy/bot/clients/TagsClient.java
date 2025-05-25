package backend.academy.bot.clients;

import static general.LogMessages.CHAT_ID_STRING;
import static general.LogMessages.STATUS;

import dto.ApiErrorResponseDTO;
import general.RetryException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class TagsClient {
    private final RestClient restClient;

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "baseCircuitBreaker")
    public String getAllTags(Long chatId) {
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
                    throw new RetryException(
                            String.valueOf(response.getStatusCode().value()));
                }
            }
        });
    }
}
