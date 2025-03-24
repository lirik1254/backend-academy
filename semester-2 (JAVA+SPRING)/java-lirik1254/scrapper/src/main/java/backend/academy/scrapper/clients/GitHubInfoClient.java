package backend.academy.scrapper.clients;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.exceptions.RepositoryNotFoundException;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ContentDTO;
import dto.UpdateType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubInfoClient {
    private final ScrapperConfig scrapperConfig;
    private final ConvertLinkToApiUtils convertLinkToApiUtils;
    private final ObjectMapper objectMapper;

    RestClient restClient = RestClient.create();

    public List<ContentDTO> getGithubContent(String link)
            throws RepositoryNotFoundException, HttpMessageNotReadableException {
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
            throw new RepositoryNotFoundException("Репозиторий не найден");
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

            return contentDTOS;
        } catch (HttpMessageNotReadableException | JsonProcessingException e) {
            log.atError()
                    .addKeyValue("link", link)
                    .setMessage("Ошибка при получении github контента")
                    .log();
            throw new HttpMessageNotReadableException("Не удаётся прочитать поле 'updated_at'");
        }
    }
}
