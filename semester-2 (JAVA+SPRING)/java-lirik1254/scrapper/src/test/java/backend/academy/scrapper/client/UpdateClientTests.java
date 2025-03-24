package backend.academy.scrapper.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import backend.academy.scrapper.TestConfig;
import backend.academy.scrapper.clients.UpdateLinkClient;
import backend.academy.scrapper.dbInitializeBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dto.ContentDTO;
import dto.UpdateDTO;
import dto.UpdateType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Тестирование клиента на /update")
@Import(TestConfig.class)
public class UpdateClientTests extends dbInitializeBase {
    protected static WireMockServer wireMockServer;

    @Autowired
    public UpdateLinkClient updateLinkClient;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

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

    @Test
    @DisplayName("Корректный запрос на update")
    public void test0() throws JsonProcessingException {
        UpdateDTO updateDTO = new UpdateDTO(
                123L,
                "https://github.com/lirik1254/abTestRepo",
                new ContentDTO(UpdateType.COMMENT, "SomeTitle", "username", "5345345435", "answer"),
                new ArrayList<>(List.of(123L)));

        wireMockServer.stubFor(post(urlEqualTo("/updates"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(updateDTO)))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")));

        updateLinkClient.sendUpdate(
                123L,
                "https://github.com/lirik1254/abTestRepo",
                new ContentDTO(UpdateType.COMMENT, "SomeTitle", "username", "5345345435", "answer"));

        wireMockServer.verify(postRequestedFor(urlEqualTo("/updates"))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(updateDTO))));
    }

    @Test
    @DisplayName("Некорректный запрос на update")
    public void test1() throws Exception {
        wireMockServer.stubFor(
                post(urlEqualTo("/updates")).willReturn(aResponse().withStatus(400)));

        mockMvc.perform(MockMvcRequestBuilders.post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"someUnknownContent\""));

        // Программа не упала
    }
}
