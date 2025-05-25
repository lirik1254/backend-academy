package backend.academy.bot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import backend.academy.bot.kafka.UpdateListener;
import backend.academy.bot.services.UpdateService;
import dto.ContentDTO;
import dto.UpdateDTO;
import dto.UpdateType;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class KafkaTest extends BaseConfigure {

    @Autowired
    private KafkaTemplate<String, UpdateDTO> kafkaTemplate;

    @MockitoBean
    private UpdateService updateService;

    @Autowired
    private ConsumerFactory<String, UpdateDTO> consumerFactory;

    @Autowired
    private UpdateListener updateListener;

    @Test
    void shouldCallUpdateService_whenMessageReceivedFromKafka() throws Exception {
        ContentDTO content = new ContentDTO(UpdateType.ISSUE, "New Issue", "user123", "2024-01-01", "Issue content");

        UpdateDTO message = new UpdateDTO(123L, "https://github.com/test", content, List.of(123L));

        kafkaTemplate.send("update-topic", message);

        Awaitility.await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            Mockito.verify(updateService, Mockito.times(1)).update(List.of(123L), "https://github.com/test", content);
        });
    }

    @Test
    void shouldSendInvalidMessageToDlt() throws Exception {
        UpdateDTO invalidUpdate = new UpdateDTO(52L, "52", new ContentDTO(null, null, null, null, null), List.of(52L));

        doThrow(new RuntimeException("boom")).when(updateService).update(anyList(), anyString(), any(ContentDTO.class));

        kafkaTemplate.send("update-topic", invalidUpdate);

        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "update-group", "true");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        try (Consumer<String, Object> consumer =
                new DefaultKafkaConsumerFactory<String, Object>(consumerProps).createConsumer()) {
            consumer.subscribe(Collections.singleton("update-topic-dlt"));

            ConsumerRecord<String, Object> record =
                    KafkaTestUtils.getSingleRecord(consumer, "update-topic-dlt", Duration.ofSeconds(10));

            assertThat(record.value()).isEqualTo(invalidUpdate);
        }
    }

    private KafkaTemplate<String, String> kafkaTemplate() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps);

        return new KafkaTemplate<>(producerFactory);
    }
}
