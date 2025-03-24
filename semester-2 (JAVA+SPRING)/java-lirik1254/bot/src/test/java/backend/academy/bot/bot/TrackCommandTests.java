package backend.academy.bot.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingJsonPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.services.TelegramBotService;
import backend.academy.bot.services.messages.State;
import backend.academy.bot.services.messages.TrackCommand;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class TrackCommandTests extends BaseConfigure {

    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private TrackCommand trackCommand;

    @Test
    @DisplayName("Если вызвал команду /track, просит ввести url")
    public void test1() {
        when(message.text()).thenReturn("/track");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите URL для отслеживания (см. /help)",
                sentMessage.getParameters().get("text"));
        assertEquals(State.WAITING_FOR_URL, trackCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Если во время ввода url вызовешь команду /stop, выйдешь из ввода url")
    public void test2() {
        when(message.text()).thenReturn("/stop");
        ReflectionTestUtils.setField(trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_URL)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Вы вышли из меню ввода ссылки", sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), trackCommand.userStates());
    }

    @Test
    @DisplayName("Если во время ввода url напишешь неверное url, попросят ввести заново")
    public void test3() {
        when(message.text()).thenReturn("https://someincorrectURL.com");
        ReflectionTestUtils.setField(trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_URL)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Некорректно введена ссылка, введите заново, либо введите /stop",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.WAITING_FOR_URL)), trackCommand.userStates());
    }

    @Test
    @DisplayName("Если введешь корректное url, попросят ввести теги")
    public void test4() {
        when(message.text()).thenReturn("https://github.com/lirik1254/abTestRepo");
        ReflectionTestUtils.setField(trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_URL)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите теги (опционально).\nЕсли теги не нужны - введите /skip",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.WAITING_FOR_TAGS)), trackCommand.userStates());
    }

    @Test
    @DisplayName("Если во время ввода тегов введешь /skip, то перейдешь к вводу фильтров")
    public void test5() {
        when(message.text()).thenReturn("/skip");
        ReflectionTestUtils.setField(trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_TAGS)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите фильтры (опционально, например, user:dummy)\nЕсли фильтры не нужны - введите /skip",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.WAITING_FOR_FILTERS)), trackCommand.userStates());
    }

    @Test
    @DisplayName("После ввода тегов переходишь к вводу фильтров")
    public void test6() {
        when(message.text()).thenReturn("Работа учёба дом");
        ReflectionTestUtils.setField(trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_TAGS)));
        ReflectionTestUtils.setField(
                trackCommand, "userUrl", new HashMap<>(Map.of(123L, "https://github.com/lirik1254/abTestRepo")));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите фильтры (опционально, например, user:dummy)\nЕсли фильтры не нужны - введите /skip",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.WAITING_FOR_FILTERS)), trackCommand.userStates());
    }

    @Test
    @DisplayName("Если вводишь фильтры в некорректном формате, попросят ввести заново")
    public void test7() {
        when(message.text()).thenReturn("Тут у фильтров нет двоеточия");
        ReflectionTestUtils.setField(
                trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_FILTERS)));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите фильтры в формате filter:filter",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.WAITING_FOR_FILTERS)), trackCommand.userStates());
    }

    @Test
    @DisplayName("После ввода фильтров пишется что репозиторий отслеживается")
    public void test8() {
        String jsonAnswer =
                """
            {
              "id": 123,
              "url": "https://github.com/lirik1254/abTestRepo",
              "tags": [],
              "filters": ["filter:filter"]
            }""";

        wireMockServer.stubFor(post(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .withRequestBody(matchingJsonPath("$.link", matching(".*github.com.*")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("filter:filter");
        ReflectionTestUtils.setField(
                trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_FILTERS)));
        ReflectionTestUtils.setField(
                trackCommand, "userUrl", new HashMap<>(Map.of(123L, "https://github.com/lirik1254/abTestRepo")));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Изменения в репозитории теперь отслеживаются",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), trackCommand.userStates());
    }

    @Test
    @DisplayName("Можно скипнуть фильтры и изменения в репозитории будут отслеживаться")
    public void test9() {
        String jsonAnswer =
                """
            {
              "id": 123,
              "url": "https://github.com/lirik1254/abTestRepo",
              "tags": [],
              "filters": ["filter:filter"]
            }""";

        wireMockServer.stubFor(post(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .withRequestBody(matchingJsonPath("$.link", matching(".*github.com.*")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/skip");
        ReflectionTestUtils.setField(
                trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_FILTERS)));
        ReflectionTestUtils.setField(
                trackCommand, "userUrl", new HashMap<>(Map.of(123L, "https://github.com/lirik1254/abTestRepo")));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Изменения в репозитории теперь отслеживаются",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), trackCommand.userStates());
    }

    @Test
    @DisplayName("Если введёшь stackoverflow, то начнется отслеживание вопроса, а не репозитория")
    public void test10() {
        String jsonAnswer =
                """
            {
              "id": 123,
              "url": "https://stackoverflow.com/questions/79467245",
              "tags": [],
              "filters": ["filter:filter"]
            }""";

        wireMockServer.stubFor(post(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .withRequestBody(matchingJsonPath("$.link", matching(".*stackoverflow.com.*")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/skip");
        ReflectionTestUtils.setField(
                trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_FILTERS)));
        ReflectionTestUtils.setField(
                trackCommand, "userUrl", new HashMap<>(Map.of(123L, "https://stackoverflow.com/questions/79467245")));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Новые ответы на вопрос теперь отслеживаются",
                sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), trackCommand.userStates());
    }

    @Test
    @DisplayName("Обработка ошибки 400 от сервера")
    public void test11() {
        String jsonAnswer =
                """
            {
                "description" : "Некорректные параметры запроса52",
                "code" : "400",
                "exceptionName" : "Некорректные параметры запроса",
                "exceptionMessage" : "Некорректные параметры запроса",
                "stacktrace" : [ "java.52", "java.25" ]
            }""";

        wireMockServer.stubFor(post(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .withRequestBody(matchingJsonPath("$.link", matching(".*stackoverflow.com.*")))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/skip");
        ReflectionTestUtils.setField(
                trackCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_FILTERS)));
        ReflectionTestUtils.setField(
                trackCommand, "userUrl", new HashMap<>(Map.of(123L, "https://stackoverflow.com/questions/79467245")));

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Некорректные параметры запроса52", sentMessage.getParameters().get("text"));
        assertEquals(new HashMap<>(Map.of(123L, State.START)), trackCommand.userStates());
    }
}
