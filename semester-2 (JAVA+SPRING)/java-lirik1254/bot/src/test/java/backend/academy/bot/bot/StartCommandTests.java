package backend.academy.bot.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.BaseConfigure;
import backend.academy.bot.services.TelegramBotService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DisplayName("Тест команды /start")
@SpringBootTest
public class StartCommandTests extends BaseConfigure {
    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Test
    @DisplayName("Тестирование команды /start")
    public void test1() {
        String jsonAnswer = "Чат зарегистрирован";

        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/start");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String returnAnswer = "Чат успешно зарегистрирован";

        assertEquals(returnAnswer, sentMessage.getParameters().get("text"));
    }

    @Test
    @DisplayName("Обработка ошибки 400 от сервера")
    public void test2() {
        String jsonAnswer =
                """
            {
                "description" : "Некорректные параметры запроса52",
                "code" : "400",
                "exceptionName" : "Некорректные параметры запроса",
                "exceptionMessage" : "Некорректные параметры запроса",
                "stacktrace" : [ "java.52", "java.25" ]
            }""";

        wireMockServer.stubFor(post(urlEqualTo("/tg-chat/123"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/start");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String returnAnswer = "Некорректные параметры запроса52";

        assertEquals(returnAnswer, sentMessage.getParameters().get("text"));
    }
}
