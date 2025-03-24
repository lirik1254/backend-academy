package backend.academy.bot.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.services.TelegramBotService;
import backend.academy.bot.services.messages.LinkTagsCommand;
import backend.academy.bot.services.messages.State;
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
public class LinkTagsTests extends BaseConfigure {
    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Autowired
    private LinkTagsCommand linkTagsCommand;

    @Test
    @DisplayName("Тестирование ввода /linktags")
    public void test1() {
        when(message.text()).thenReturn("/linktags");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(
                "Введите теги через пробел, по которым вы хотите вывести ссылки",
                sentMessage.getParameters().get("text"));
        assertEquals(State.WAITING_FOR_LINK_TAGS, linkTagsCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Ввод /stop после /linktags")
    public void test2() {
        when(message.text()).thenReturn("/stop");
        ReflectionTestUtils.setField(
                linkTagsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_LINK_TAGS)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, atLeastOnce()).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals("Вы вышли из меню ввода тегов", sentMessage.getParameters().get("text"));
        assertEquals(State.START, linkTagsCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Ввод корректного тега после /linktags - выведет ссылки")
    public void test3() {
        String jsonAnswer =
                """
                {
                    "links": [
                        {
                            "id": 1,
                            "url": "https://github.com/lirik1254/abTestRepo",
                            "tags": [
                                "tag",
                                "my"
                            ],
                            "filters": [
                                "my:filters"
                            ]
                        }
                    ],
                    "size": 1
                }""";

        String returnAnswer =
                """
            Кол-во отслеживаемых ссылок: 1

            Отслеживаемые ссылки:

            1) Ссылка: https://github.com/lirik1254/abTestRepo
            Теги: tag, my
            Фильтры: my:filters""";

        wireMockServer.stubFor(post(urlEqualTo("/link-tags/123"))
                .withRequestBody(equalToJson("[\"my\"]"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("my");
        ReflectionTestUtils.setField(
                linkTagsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_LINK_TAGS)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(returnAnswer, sentMessage.getParameters().get("text"));
        assertEquals(State.START, linkTagsCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Ввод несуществующего тега - выведет сообщение, что ссылок нет")
    public void test4() {
        String jsonAnswer =
                """
                {
                    "links": [],
                    "size": 0
                }""";

        String returnAnswer = "Нет ссылок по предложенным тегам";

        wireMockServer.stubFor(post(urlEqualTo("/link-tags/123"))
                .withRequestBody(equalToJson("[\"mysadf\"]"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("mysadf");
        ReflectionTestUtils.setField(
                linkTagsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_LINK_TAGS)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(returnAnswer, sentMessage.getParameters().get("text"));
        assertEquals(State.START, linkTagsCommand.userStates().get(123L));
    }

    @Test
    @DisplayName("Некорректный ответ от сервера")
    public void test5() {
        String jsonAnswer = """
                {
                    55пиидисяя тдвааа
                }""";

        String returnAnswer = "Ошибка";

        wireMockServer.stubFor(post(urlEqualTo("/link-tags/123"))
                .withRequestBody(equalToJson("[\"mysadf\"]"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("mysadf");
        ReflectionTestUtils.setField(
                linkTagsCommand, "userStates", new HashMap<>(Map.of(123L, State.WAITING_FOR_LINK_TAGS)));
        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(returnAnswer, sentMessage.getParameters().get("text"));
        assertEquals(State.START, linkTagsCommand.userStates().get(123L));
    }
}
