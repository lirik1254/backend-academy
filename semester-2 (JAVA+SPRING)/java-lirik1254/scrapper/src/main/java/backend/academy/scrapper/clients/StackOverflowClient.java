package backend.academy.scrapper.clients;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.exceptions.QuestionNotFoundException;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ContentDTO;
import dto.UpdateType;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@Slf4j
public class StackOverflowClient {
    private final ScrapperConfig scrapperConfig;
    private final ConvertLinkToApiUtils convertLinkToApiUtils;
    private final ObjectMapper objectMapper;
    private final RestClient restClient = RestClient.create();

    private final String key;
    private final String access_token;
    private final String filter;

    private static final String JSON_ERROR = "Ошибка при разборе JSON-Ответа";
    private static final String REQUEST_ERROR = "Ошибка при запросе";

    public StackOverflowClient(
            ScrapperConfig scrapperConfig, ConvertLinkToApiUtils convertLinkToApiUtils, ObjectMapper objectMapper) {
        this.scrapperConfig = scrapperConfig;
        this.convertLinkToApiUtils = convertLinkToApiUtils;
        this.objectMapper = objectMapper;

        this.key = scrapperConfig.stackOverflow().key();
        this.access_token = scrapperConfig.stackOverflow().accessToken();
        this.filter = "withbody";
    }

    private String performRequest(String url) {
        url += "&key=" + key + "&access_token=" + access_token + "&filter=" + filter;

        try {
            return restClient.get().uri(url).retrieve().body(String.class);
        } catch (RestClientResponseException e) {
            log.atError().addKeyValue("link", url).setMessage(REQUEST_ERROR).log();
            throw new QuestionNotFoundException("Ошибка при запросе: " + url);
        }
    }

    private List<ContentDTO> parseContentList(String jsonResponse, UpdateType type, String title, String url) {
        List<ContentDTO> contentDTOS = new ArrayList<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            jsonNode.get("items").forEach(item -> {
                String ownerName = item.get("owner").get("display_name").asText();
                String body = item.get("body").asText();
                String creationDate = item.get("creation_date").asText();
                contentDTOS.add(new ContentDTO(type, title, ownerName, creationDate, body));
            });
        } catch (Exception e) {
            log.atError().addKeyValue("link", url).setMessage(JSON_ERROR).log();
            throw new HttpMessageNotReadableException("Ошибка при разборе JSON-ответа по URL " + url);
        }
        return contentDTOS;
    }

    public String getTitle(String link) {
        String url = convertLinkToApiUtils.convertSOtoQuestion(link);
        String jsonResponse = performRequest(url);
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            return jsonNode.get("items").get(0).get("title").asText();
        } catch (HttpMessageNotReadableException | JsonProcessingException e) {
            log.atError().addKeyValue("link", url).setMessage(JSON_ERROR).log();
            throw new HttpMessageNotReadableException("Ошибка при разборе JSON-ответа по URL " + url);
        } catch (Exception e) {
            log.atError()
                    .addKeyValue("link", url)
                    .setMessage("Некорректное тело запроса")
                    .log();
            throw new RuntimeException("Некорректное тело запроса по URL " + url);
        }
    }

    private List<ContentDTO> getComments(String link, String title) {
        String soUrl = convertLinkToApiUtils.convertSOtoQuestionComments(link);
        return parseContentList(performRequest(soUrl), UpdateType.COMMENT, title, soUrl);
    }

    private List<ContentDTO> getAnswers(String link, String title) {
        String soUrl = convertLinkToApiUtils.convertSOtoQuestionAnswers(link);
        return parseContentList(performRequest(soUrl), UpdateType.ANSWER, title, soUrl);
    }

    private List<ContentDTO> getAnswerComments(String link, String title) {
        List<ContentDTO> contentDTOS = new ArrayList<>();
        String url = convertLinkToApiUtils.convertSOtoQuestionAnswers(link);
        String jsonResponse = performRequest(url);

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            jsonNode.get("items").forEach(item -> {
                String answerId = item.get("answer_id").asText();
                String commentsUrl = convertLinkToApiUtils.convertSOtoAnswerCommentsLink(answerId, link);
                contentDTOS.addAll(
                        parseContentList(performRequest(commentsUrl), UpdateType.COMMENT, title, commentsUrl));
            });
        } catch (HttpMessageNotReadableException | JsonProcessingException e) {
            log.atError().addKeyValue("link", url).setMessage(REQUEST_ERROR).log();
            throw new HttpMessageNotReadableException(e.getMessage());
        } catch (Exception e) {
            log.atError().addKeyValue("link", url).setMessage(REQUEST_ERROR).log();
            throw new QuestionNotFoundException(e.getMessage());
        }

        return contentDTOS;
    }

    public List<ContentDTO> getSOContent(String link) {
        String title = getTitle(link);

        List<ContentDTO> contentDTOS = new ArrayList<>();
        contentDTOS.addAll(getComments(link, title));
        contentDTOS.addAll(getAnswers(link, title));
        contentDTOS.addAll(getAnswerComments(link, title));

        return contentDTOS;
    }
}
