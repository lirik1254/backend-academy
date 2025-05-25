package backend.academy.scrapper.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {
    @Value("${app.topic}")
    private String topic;

    @Bean
    public NewTopic updateTopic() {
        return TopicBuilder.name(topic).build();
    }
}
