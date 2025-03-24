package backend.academy.bot.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.services.TelegramBotService;
import backend.academy.bot.services.messages.State;
import backend.academy.bot.services.messages.UntrackCommand;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class UntrackCommandTests {
    protected static WireMockServer wireMockServer;
    protected static Update update;
    protected static Message message;
    protected static Chat chat;

    // Новый WireMock, потому что почему-то не проходит через mvn test если класс унаследовать от BaseConfigure
    // В студии однако всё проходит

    @BeforeAll
    static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8087));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8087);

        update = Mockito.mock(Update.class);
        message = Mockito.mock(Message.class);
        chat = Mockito.mock(Chat.class);

        when(update.message()).thenReturn(message);
        when(message.chat()).thenReturn(chat);
        when(chat.id()).thenReturn(123L);
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.base-url", () -> "http://localhost:8087");
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private UntrackCommand untrackCommand;

    @Test
    @DisplayName("Тестирование команды /untrack")
    public void test1() {
        when(message.text()).thenReturn("/untrack");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите URL для прекращения отслеживания (см. /help)",
                sentMessage.getParameters().get("text"));
        assertEquals(State.WAITING_FOR_URL, untrackCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Ввод /stop после /untrack")
    public void test2() {
        when(message.text()).thenReturn("/stop");
        ReflectionTestUtils.setField(untrackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_URL)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, atLeastOnce()).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Вы вышли из меню ввода ссылки", sentMessage.getParameters().get("text"));
        assertEquals(State.START, untrackCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Ввод ссылки после /untrack - пишет, что репозиторий больше не отслеживается")
    public void test3() {
        String jsonAnswer =
                """
            {
               "id" : 123,
               "url" : "https://github.com/lirik1254/abTestRepo",
               "tags" : [ "haamoooodahamibihamood" ],
               "filters" : [ "ya:ustal" ]
            }""";

        wireMockServer.stubFor(delete(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .withRequestBody(matchingJsonPath("$.link", equalTo("https://github.com/lirik1254/abTestRepo")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("https://github.com/lirik1254/abTestRepo");
        ReflectionTestUtils.setField(
                untrackCommand, "userStates", new ConcurrentHashMap<>(Map.of(123L, State.WAITING_FOR_URL)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Изменения в репозитории больше не отслеживается",
                sentMessage.getParameters().get("text"));
        assertEquals(State.START, untrackCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Ввод ссылки после /untrack - пишет, что вопрос больше не отслеживается")
    public void test4() {
        String jsonAnswer =
                """
            {
               "id" : 123,
               "url" : "https://stackoverflow.com/questions/79474325",
               "tags" : [ "haamoooodahamibihamood" ],
               "filters" : [ "ya:ustal" ]
            }""";

        wireMockServer.stubFor(delete(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .withRequestBody(matchingJsonPath("$.link", equalTo("https://stackoverflow.com/questions/79474325")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("https://stackoverflow.com/questions/79474325");
        ReflectionTestUtils.setField(
                untrackCommand, "userStates", new ConcurrentHashMap<>(Map.of(123L, State.WAITING_FOR_URL)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Изменения в вопросе больше не отслеживаются",
                sentMessage.getParameters().get("text"));
        assertEquals(State.START, untrackCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Обработка ошибки 400 от сервера")
    public void test5() throws InterruptedException {
        String jsonAnswer =
                """
            {
                "description" : "Некорректные параметры запроса52",
                "code" : "400",
                "exceptionName" : "Некорректные параметры запроса",
                "exceptionMessage" : "Некорректные параметры запроса",
                "stacktrace" : [ "java.52", "java.25" ]
            }""";

        wireMockServer.stubFor(delete(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .withRequestBody(matchingJsonPath("$.link", equalTo("https://github.com/lirik1254/abTestRepo")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("https://github.com/lirik1254/abTestRepo");
        ReflectionTestUtils.setField(untrackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_URL)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Некорректные параметры запроса52", sentMessage.getParameters().get("text"));
        assertEquals(State.START, untrackCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Обработка ошибки 404 от сервера")
    public void test6() throws InterruptedException {
        String jsonAnswer =
                """
            {
                "description" : "Ссылка не найдена",
                "code" : "404",
                "exceptionName" : "Ссылка не найдена",
                "exceptionMessage" : "Ссылка не найдена",
                "stacktrace" : [ "java.52", "java.25" ]
            }""";

        wireMockServer.stubFor(delete(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .withRequestBody(matchingJsonPath("$.link", equalTo("https://github.com/lirik1254/abTestRepo")))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("https://github.com/lirik1254/abTestRepo");
        ReflectionTestUtils.setField(untrackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_URL)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals("Ссылка не найдена", sentMessage.getParameters().get("text"));
        assertEquals(State.START, untrackCommand.userStates().get(123L));
    }
}
