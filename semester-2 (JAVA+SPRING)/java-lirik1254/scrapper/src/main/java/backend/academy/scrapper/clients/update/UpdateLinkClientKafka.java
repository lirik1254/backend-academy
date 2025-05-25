package backend.academy.scrapper.clients.update;

import backend.academy.scrapper.clients.UpdateLinkClient;
import dto.ContentDTO;
import dto.UpdateDTO;
import general.RetryException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateLinkClientKafka implements UpdateLinkClient {
    private final KafkaTemplate<String, UpdateDTO> kafkaTemplate;

    public UpdateLinkClientKafka(KafkaTemplate<String, UpdateDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendUpdate(Long chatId, String link, ContentDTO contentDTO) {
        try {
            kafkaTemplate
                    .send("update-topic", new UpdateDTO(chatId, link, contentDTO, List.of(chatId)))
                    .get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RetryException("500");
        } catch (ExecutionException e) {
            log.atError().setMessage(e.getMessage()).log();
            throw new RetryException("500");
        }
    }
}
