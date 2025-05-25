package backend.academy.bot.kafka;

import backend.academy.bot.services.UpdateService;
import dto.UpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateListener {
    private final UpdateService updateService;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(groupId = "update-group", topics = "update-topic", containerFactory = "getUpdateListener")
    public void sendUpdate(UpdateDTO updateDTO) {
        updateService.update(updateDTO.tgChatIds(), updateDTO.url(), updateDTO.contentDTO());
    }

    @DltHandler
    public void dltHandler(UpdateDTO dto, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("DLT received {} from topic {}", dto, topic);
    }
}
