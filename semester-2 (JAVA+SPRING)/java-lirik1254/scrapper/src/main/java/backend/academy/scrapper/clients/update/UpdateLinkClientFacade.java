package backend.academy.scrapper.clients.update;

import backend.academy.scrapper.clients.UpdateLinkClient;
import dto.ContentDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateLinkClientFacade implements UpdateLinkClient {
    private final UpdateLinkClient httpClient;
    private final UpdateLinkClient kafkaClient;
    private UpdateLinkClient currentTransport;

    @Value("${app.message-transport}")
    private String transportType;

    public UpdateLinkClientFacade(
            @Qualifier("updateLinkClientHTTP") UpdateLinkClient httpClient,
            @Qualifier("updateLinkClientKafka") UpdateLinkClient kafkaClient) {
        this.httpClient = httpClient;
        this.kafkaClient = kafkaClient;
    }

    @PostConstruct
    public void init() {
        this.currentTransport = switch (transportType.toUpperCase()) {
            case "kafka" -> kafkaClient;
            default -> httpClient;
        };
    }

    @Override
    @Retry(name = "defaultRetry", fallbackMethod = "switchTransport")
    @CircuitBreaker(name = "baseCircuitBreaker")
    public void sendUpdate(Long chatId, String link, ContentDTO contentDTO) {
        currentTransport.sendUpdate(chatId, link, contentDTO);
    }

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "baseCircuitBreaker")
    public void switchTransport(Long chatId, String link, ContentDTO contentDTO, Throwable t) {
        UpdateLinkClient newTransport = (currentTransport == httpClient) ? kafkaClient : httpClient;

        newTransportSendUpdate(chatId, link, contentDTO, newTransport);
    }

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "baseCircuitBreaker")
    public void newTransportSendUpdate(
            Long chatId, String link, ContentDTO contentDTO, UpdateLinkClient updateLinkClient) {
        updateLinkClient.sendUpdate(chatId, link, contentDTO);
    }
}
