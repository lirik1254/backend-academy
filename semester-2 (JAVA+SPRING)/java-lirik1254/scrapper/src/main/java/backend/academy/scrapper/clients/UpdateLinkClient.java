package backend.academy.scrapper.clients;

import backend.academy.scrapper.ScrapperConfig;
import dto.ApiErrorResponseDTO;
import dto.ContentDTO;
import dto.UpdateDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UpdateLinkClient {
    private final ScrapperConfig scrapperConfig;
    RestClient restClient;

    public UpdateLinkClient(ScrapperConfig scrapperConfig) {
        this.scrapperConfig = scrapperConfig;
        restClient = RestClient.create(scrapperConfig.baseUrl());
    }

    public void sendUpdate(Long chatId, String link, ContentDTO contentDTO) {
        try {
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
                        }
                        return Mono.empty();
                    });
        } catch (Exception e) {
            log.atError()
                    .addKeyValue("chatId", chatId)
                    .addKeyValue("link", link)
                    .setMessage("Произошла ошибка сервера при отправке сообщения по ссылке")
                    .log();
        }
    }
}
