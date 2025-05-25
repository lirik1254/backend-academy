package backend.academy.scrapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import backend.academy.scrapper.clients.update.UpdateLinkClientKafka;
import dto.ContentDTO;
import dto.UpdateDTO;
import dto.UpdateType;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

class KafkaTest extends ExternalInitBase {
    @Autowired
    private KafkaTemplate<String, UpdateDTO> kafkaTemplate;

    @Autowired
    private UpdateLinkClientKafka updateLinkClientKafka;

    @Autowired
    private ConsumerFactory<String, UpdateDTO> consumerFactory;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.message-transport", () -> "Kafka");
    }

    @Test
    void shouldSuccessfullySendMessageToKafkaTopic() throws Exception {
        ContentDTO content = new ContentDTO(UpdateType.ISSUE, "New Issue", "user123", "2024-01-01", "Issue content");

        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-group", "true");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        updateLinkClientKafka.sendUpdate(123L, "https://github.com/test", content);

        try (Consumer<String, UpdateDTO> consumer =
                new DefaultKafkaConsumerFactory<String, UpdateDTO>(consumerProps).createConsumer()) {
            consumer.subscribe(Collections.singleton("update-topic"));

            ConsumerRecord<String, UpdateDTO> record =
                    KafkaTestUtils.getSingleRecord(consumer, "update-topic", Duration.ofSeconds(20));

            assertThat(record.value()).satisfies(update -> {
                assertThat(update.tgChatIds()).isEqualTo(List.of(123L));
                assertThat(update.url()).isEqualTo("https://github.com/test");
                assertThat(update.contentDTO().title()).isEqualTo("New Issue");
            });
        }
    }
}
