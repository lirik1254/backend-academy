package backend.academy.scrapper.clients.update;

import backend.academy.scrapper.clients.UpdateLinkClient;
import dto.ApiErrorResponseDTO;
import dto.ContentDTO;
import dto.UpdateDTO;
import general.RetryException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UpdateLinkClientHTTP implements UpdateLinkClient {
    private final RestClient restClient;

    @Autowired
    public UpdateLinkClientHTTP(@Qualifier("baseUrl") RestClient restClient) {
        this.restClient = restClient;
    }

    public void sendUpdate(Long chatId, String link, ContentDTO contentDTO) {
        restClient
                .post()
                .uri("/updates")
                .body(new UpdateDTO(chatId, link, contentDTO, List.of(chatId)))
                .exchange((request, response) -> {
                    if (!response.getStatusCode().is2xxSuccessful()) {
                        ApiErrorResponseDTO apiErrorResponseDTO = response.bodyTo(ApiErrorResponseDTO.class);
                        if (apiErrorResponseDTO != null) {
                            log.atError()
                                    .addKeyValue("description", apiErrorResponseDTO.description())
                                    .addKeyValue("chatId", chatId)
                                    .addKeyValue("link", link)
                                    .setMessage("Ошибка при отправке сообщения")
                                    .log();
                            log.error("Ошибка при отправке сообщения: {}", apiErrorResponseDTO);
                        } else {
                            log.atError()
                                    .addKeyValue("link", link)
                                    .addKeyValue("chatId", chatId)
                                    .setMessage("Не удалось отправить обновление по ссылке")
                                    .log();
                        }
                        throw new RetryException(
                                String.valueOf(response.getStatusCode().value()));
                    }
                    return Mono.empty();
                });
    }
}
