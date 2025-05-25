package backend.academy.scrapper.client;

import static backend.academy.scrapper.client.StackOverflowRetString.FIRST_ANSWER_COMMENTS;
import static backend.academy.scrapper.client.StackOverflowRetString.RETURN_ANSWER_STRING;
import static backend.academy.scrapper.client.StackOverflowRetString.RETURN_TITLE_STRING;
import static backend.academy.scrapper.client.StackOverflowRetString.SECOND_ANSWER_COMMENTS;
import static backend.academy.scrapper.client.StackOverflowRetString.SO_TO_ANSWER_COMMENTS_152;
import static backend.academy.scrapper.client.StackOverflowRetString.SO_TO_ANSWER_COMMENTS_52;
import static backend.academy.scrapper.client.StackOverflowRetString.SO_TO_QUESTION_ANSWERS;
import static backend.academy.scrapper.client.StackOverflowRetString.SO_TO_QUESTION_COMMENTS_LINK;
import static backend.academy.scrapper.client.StackOverflowRetString.SO_TO_QUESTION_LINK;
import static backend.academy.scrapper.client.StackOverflowRetString.WIREMOCK_SO_TO_ANSWER_COMMENTS_152;
import static backend.academy.scrapper.client.StackOverflowRetString.WIREMOCK_SO_TO_ANSWER_COMMENTS_52;
import static backend.academy.scrapper.client.StackOverflowRetString.WIREMOCK_SO_TO_QUESTION_ANSWERS_LINK;
import static backend.academy.scrapper.client.StackOverflowRetString.WIREMOCK_SO_TO_QUESTION_COMMENTS_LINK;
import static backend.academy.scrapper.client.StackOverflowRetString.WIREMOCK_SO_TO_QUESTION_LINK;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.ExternalInitBase;
import backend.academy.scrapper.TestConfig;
import backend.academy.scrapper.clients.StackOverflowClient;
import backend.academy.scrapper.exceptions.QuestionNotFoundException;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dto.ContentDTO;
import dto.UpdateType;
import general.RetryException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest(
        properties = {
            "resilience4j.retry.instances.defaultRetry.max-attempts=1",
            "resilience4j.ratelimiter.configs.defaultConfig.limit-for-period=3000"
        })
@RequiredArgsConstructor
@Import(TestConfig.class)
@DisplayName("Тестирование StackOverflow клиента")
public class StackOverflowClientTests extends ExternalInitBase {
    protected static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterAll
    public static void close() {
        wireMockServer.stop();
    }

    @MockitoBean
    ConvertLinkToApiUtils convertLinkToApiUtils;

    @Autowired
    StackOverflowClient stackOverflowClient;

    @BeforeEach
    public void setUpEach() {
        when(convertLinkToApiUtils.convertSOtoQuestion(anyString())).thenReturn(SO_TO_QUESTION_LINK);
        when(convertLinkToApiUtils.convertSOtoQuestionComments(anyString())).thenReturn(SO_TO_QUESTION_COMMENTS_LINK);
        when(convertLinkToApiUtils.convertSOtoQuestionAnswers(anyString())).thenReturn(SO_TO_QUESTION_ANSWERS);
        when(convertLinkToApiUtils.convertSOtoAnswerCommentsLink(eq("152"), anyString()))
                .thenReturn(SO_TO_ANSWER_COMMENTS_152);
        when(convertLinkToApiUtils.convertSOtoAnswerCommentsLink(eq("52"), anyString()))
                .thenReturn(SO_TO_ANSWER_COMMENTS_52);

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_ANSWERS_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(RETURN_ANSWER_STRING)));

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_ANSWER_COMMENTS_52))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(FIRST_ANSWER_COMMENTS)));

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_ANSWER_COMMENTS_152))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(SECOND_ANSWER_COMMENTS)));

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(RETURN_TITLE_STRING)));

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_COMMENTS_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(FIRST_ANSWER_COMMENTS)));
    }

    @Test
    @DisplayName("Тестирование получения заголовка")
    public void test0() {
        assertEquals("52", stackOverflowClient.getTitle(SO_TO_QUESTION_LINK));
    }

    @Test
    @DisplayName("Тестирование получения заголовка - ответ 400 от SO")
    public void test1() {
        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("bad request")));

        RetryException retryException =
                assertThrows(RetryException.class, () -> stackOverflowClient.getTitle(SO_TO_QUESTION_LINK));

        assertEquals("400", retryException.getMessage());
    }

    @Test
    @DisplayName("Тестирование получения заголовка - ответ 200 с некорректным телом")
    public void test52() {
        String exceptionMessage = "Ошибка при разборе JSON-ответа по URL"
                + " http://localhost:8080/stackoverflow/questions/52?site=ru.stackoverflow.com";

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("something incorrect structure")));

        HttpMessageNotReadableException httpMessageNotReadableException = assertThrows(
                HttpMessageNotReadableException.class, () -> stackOverflowClient.getTitle(SO_TO_QUESTION_LINK));

        assertEquals(exceptionMessage, httpMessageNotReadableException.getMessage());
    }

    @Test
    @DisplayName("Тестирование получения комментариев")
    public void test2() {
        List<ContentDTO> result = ReflectionTestUtils.invokeMethod(
                stackOverflowClient, "getComments", SO_TO_QUESTION_COMMENTS_LINK, "52");

        assertEquals(
                List.of(
                        new ContentDTO(UpdateType.COMMENT, "52", "lirik1254", "1742078859", "first_comment"),
                        new ContentDTO(UpdateType.COMMENT, "52", "andrey545454", "1445078859", "second_comment")),
                result);
    }

    @Test
    @DisplayName("Получение комментариев - ответ 400")
    public void test54() {
        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_COMMENTS_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("Bad Request")));

        RetryException retryException = assertThrows(
                RetryException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        stackOverflowClient, "getComments", SO_TO_QUESTION_COMMENTS_LINK, "52"));

        assertEquals("400", retryException.getMessage());
    }

    @Test
    @DisplayName("Получение комментариев - ответ 200, некорректное тело")
    public void test55() {
        String exceptionMessage = "Ошибка при разборе JSON-ответа по URL"
                + " http://localhost:8080/stackoverflow/questions/52/comments?site=ru.stackoverflow.com";

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_COMMENTS_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("incorrect body")));

        HttpMessageNotReadableException httpMessageNotReadableException = assertThrows(
                HttpMessageNotReadableException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        stackOverflowClient, "getComments", SO_TO_QUESTION_COMMENTS_LINK, "52"));

        assertEquals(exceptionMessage, httpMessageNotReadableException.getMessage());
    }

    @Test
    @DisplayName("Тестирование получения ответов")
    public void test33() {
        List<ContentDTO> result =
                ReflectionTestUtils.invokeMethod(stackOverflowClient, "getAnswers", SO_TO_QUESTION_ANSWERS, "52");

        assertEquals(
                List.of(
                        new ContentDTO(UpdateType.ANSWER, "52", "lirik1254", "1742078859", "piisyat dva"),
                        new ContentDTO(UpdateType.ANSWER, "52", "another", "1445078859", "another_answer")),
                result);
    }

    @Test
    @DisplayName("Получение ответа - 400 ошибка")
    public void test3() {
        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_ANSWERS_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("Bad Request")));

        RetryException questionNotFoundException = assertThrows(
                RetryException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        stackOverflowClient, "getAnswers", SO_TO_QUESTION_ANSWERS, "52"));

        assertEquals("400", questionNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Получение ответа - 200 ответ, некорректные параметры")
    public void test31() {
        String exceptionMessage = "Ошибка при разборе JSON-ответа "
                + "по URL http://localhost:8080/stackoverflow/questions/52/answers?site=ru.stackoverflow.com";

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_QUESTION_ANSWERS_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("incorrect body")));

        HttpMessageNotReadableException httpMessageNotReadableException = assertThrows(
                HttpMessageNotReadableException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        stackOverflowClient, "getAnswers", SO_TO_QUESTION_ANSWERS, "52"));

        assertEquals(exceptionMessage, httpMessageNotReadableException.getMessage());
    }

    @Test
    @DisplayName("Тестирование получения комментариев по всем ответам")
    public void test4() {
        List<ContentDTO> result = ReflectionTestUtils.invokeMethod(
                stackOverflowClient, "getAnswerComments", SO_TO_QUESTION_ANSWERS, "52");

        List<ContentDTO> expectedResult = List.of(
                new ContentDTO(UpdateType.COMMENT, "52", "lirik1254", "1742078859", "third_comment"),
                new ContentDTO(UpdateType.COMMENT, "52", "another", "1445078859", "fourth_comment"),
                new ContentDTO(UpdateType.COMMENT, "52", "lirik1254", "1742078859", "first_comment"),
                new ContentDTO(UpdateType.COMMENT, "52", "andrey545454", "1445078859", "second_comment"));

        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Получение комментариев по всем ответам - 400 ошибка")
    public void test30() {
        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_ANSWER_COMMENTS_52))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("Bad Request")));

        QuestionNotFoundException questionNotFoundException = assertThrows(
                QuestionNotFoundException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        stackOverflowClient, "getAnswerComments", SO_TO_ANSWER_COMMENTS_52, "52"));

        assertEquals("400", questionNotFoundException.getMessage());
    }

    @Test
    @DisplayName("Получение комментариев по всем ответам - 200 ответ некорректное тело")
    public void test305() {
        String exceptionMessage = "Ошибка при разборе JSON-ответа по URL"
                + " http://localhost:8080/stackoverflow/answers/52/comments?site=ru.stackoverflow.com";

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_SO_TO_ANSWER_COMMENTS_52))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("incorrect body")));

        HttpMessageNotReadableException httpMessageNotReadableException = assertThrows(
                HttpMessageNotReadableException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        stackOverflowClient, "getAnswerComments", SO_TO_ANSWER_COMMENTS_52, "52"));

        assertEquals(exceptionMessage, httpMessageNotReadableException.getMessage());
    }

    @Test
    @DisplayName("Тестирование получения всего контента StackOverflow")
    public void test5() {
        List<ContentDTO> result = stackOverflowClient.getSOContent(SO_TO_QUESTION_LINK);

        List<ContentDTO> expectedResult = List.of(
                new ContentDTO(UpdateType.COMMENT, "52", "lirik1254", "1742078859", "first_comment"),
                new ContentDTO(UpdateType.COMMENT, "52", "andrey545454", "1445078859", "second_comment"),
                new ContentDTO(UpdateType.ANSWER, "52", "lirik1254", "1742078859", "piisyat dva"),
                new ContentDTO(UpdateType.ANSWER, "52", "another", "1445078859", "another_answer"),
                new ContentDTO(UpdateType.COMMENT, "52", "lirik1254", "1742078859", "third_comment"),
                new ContentDTO(UpdateType.COMMENT, "52", "another", "1445078859", "fourth_comment"),
                new ContentDTO(UpdateType.COMMENT, "52", "lirik1254", "1742078859", "first_comment"),
                new ContentDTO(UpdateType.COMMENT, "52", "andrey545454", "1445078859", "second_comment"));

        assertEquals(expectedResult, result);
    }
}
