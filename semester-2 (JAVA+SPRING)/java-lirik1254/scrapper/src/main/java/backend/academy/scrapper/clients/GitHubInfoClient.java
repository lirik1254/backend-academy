package backend.academy.scrapper.clients;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.exceptions.RepositoryNotFoundException;
import backend.academy.scrapper.micrometer.link.time.LinkTimeMetric;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import backend.academy.scrapper.utils.LinkType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ContentDTO;
import dto.UpdateType;
import general.RetryException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Timer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@Slf4j
public class GitHubInfoClient {
    private final ScrapperConfig scrapperConfig;
    private final ConvertLinkToApiUtils convertLinkToApiUtils;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;
    private final LinkTimeMetric linkTimeMetric;

    public GitHubInfoClient(
            ScrapperConfig scrapperConfig,
            ConvertLinkToApiUtils convertLinkToApiUtils,
            ObjectMapper objectMapper,
            @Qualifier("default") RestClient restClient,
            LinkTimeMetric linkTimeMetric) {
        this.scrapperConfig = scrapperConfig;
        this.convertLinkToApiUtils = convertLinkToApiUtils;
        this.objectMapper = objectMapper;
        this.restClient = restClient;
        this.linkTimeMetric = linkTimeMetric;
    }

    @Retry(name = "defaultRetry")
    @CircuitBreaker(name = "baseCircuitBreaker")
    public List<ContentDTO> getGithubContent(String link)
            throws RepositoryNotFoundException, HttpMessageNotReadableException {

        Timer.Sample sample = Timer.start(linkTimeMetric.registry());
        String response = "";
        try {
            response = restClient
                    .get()
                    .uri(convertLinkToApiUtils.convertGithubLinkToIssueApi(link))
                    .header("Accept", "application/vnd.github+json")
                    .header("Authorization", "Bearer " + scrapperConfig.githubToken())
                    .retrieve()
                    .body(String.class);
        } catch (RestClientResponseException e) {
            log.atError()
                    .addKeyValue("link", link)
                    .setMessage("Не удалось найти репозиторий")
                    .log();
            throw new RetryException(String.valueOf(e.getStatusCode().value()));
        }

        try {
            JsonNode jsonResponse = objectMapper.readTree(response);

            List<ContentDTO> contentDTOS = new ArrayList<>();

            jsonResponse.forEach(content -> {
                String title = content.get("title").asText();
                String userName = content.get("user").get("login").asText();
                String createdAt = content.get("created_at").asText();
                String body = content.get("body").asText();
                body = body.equals("null") ? "" : body;
                if (content.has("pull_request")) {
                    contentDTOS.add(new ContentDTO(UpdateType.PR, title, userName, createdAt, body));
                } else {
                    contentDTOS.add(new ContentDTO(UpdateType.ISSUE, title, userName, createdAt, body));
                }
            });

            Collections.reverse(contentDTOS);
            return contentDTOS;
        } catch (HttpMessageNotReadableException | JsonProcessingException e) {
            log.atError()
                    .addKeyValue("link", link)
                    .setMessage("Ошибка при получении github контента")
                    .log();
            throw new HttpMessageNotReadableException("Не удаётся прочитать поле 'updated_at'");
        } finally {
            sample.stop(linkTimeMetric.getTimer(LinkType.GITHUB));
        }
    }
}
