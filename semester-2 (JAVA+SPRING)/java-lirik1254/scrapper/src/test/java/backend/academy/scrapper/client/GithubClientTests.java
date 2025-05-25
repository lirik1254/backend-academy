package backend.academy.scrapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEFAULTS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import backend.academy.scrapper.ExternalInitBase;
import backend.academy.scrapper.TestConfig;
import backend.academy.scrapper.clients.GitHubInfoClient;
import backend.academy.scrapper.utils.ConvertLinkToApiUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dto.ContentDTO;
import dto.UpdateType;
import general.RetryException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        properties = {
            "resilience4j.retry.instances.defaultRetry.max-attempts=1",
            "resilience4j.ratelimiter.configs.defaultConfig.limit-for-period=3000"
        })
@DisplayName("Тестирование Github клиента")
@Import(TestConfig.class)
@Testcontainers
public class GithubClientTests extends ExternalInitBase {
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

    @Autowired
    GitHubInfoClient gitHubInfoClient;

    @MockitoBean
    ConvertLinkToApiUtils convertLinkToApiUtils =
            Mockito.mock(ConvertLinkToApiUtils.class, withSettings().defaultAnswer(invocation -> {
                if (invocation.getMethod().getName().equals("convertGithubLinkToApi")) {
                    return LINK;
                }
                return RETURNS_DEFAULTS.answer(invocation);
            }));

    private static final String LINK = "http://localhost:8080/repos/lirik1254/abTestRepo/issues";

    private static final String WIREMOCK_STUB_LINK = "/repos/lirik1254/abTestRepo/issues";

    @BeforeEach
    public void BeforeEachSetUp() {
        when(convertLinkToApiUtils.convertGithubLinkToIssueApi(anyString())).thenReturn(LINK);
    }

    @Test
    @DisplayName("Корректный ответ от github.api, тип issue")
    public void test1() {
        String returnAnswer =
                """
            [
                {
                    "title" : "issue_title",
                    "user" : {
                                "login" : "lirik1254"
                             },
                    "created_at" : "2025-03-14T19:14:31Z",
                    "body" : "issue"
                }
            ]""";

        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_STUB_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(returnAnswer)));

        List<ContentDTO> answer = gitHubInfoClient.getGithubContent(LINK);

        assertEquals(1, gitHubInfoClient.getGithubContent(LINK).size());
        assertEquals(
                new ContentDTO(UpdateType.ISSUE, "issue_title", "lirik1254", "2025-03-14T19:14:31Z", "issue"),
                answer.getFirst());
    }

    @Test
    @DisplayName("Некорректный ответ от github.api с 400 ответом")
    public void test2() {
        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_STUB_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("""
                    incorrect json answer""")));
        assertThrows(RetryException.class, () -> gitHubInfoClient.getGithubContent(LINK));
    }

    @Test
    @DisplayName("Некорректный ответ от github.api с 200 ответом")
    public void test3() {
        wireMockServer.stubFor(get(urlEqualTo(WIREMOCK_STUB_LINK))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                    incorrect json answer""")));

        assertThrows(HttpMessageNotReadableException.class, () -> gitHubInfoClient.getGithubContent(LINK));
    }
}
