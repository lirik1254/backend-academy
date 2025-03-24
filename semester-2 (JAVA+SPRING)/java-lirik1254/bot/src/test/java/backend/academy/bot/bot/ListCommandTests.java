package backend.academy.bot.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.services.TelegramBotService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class ListCommandTests extends BaseConfigure {
    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Test
    @DisplayName("Тестирование вывода всех отслеживаемых ссылок (1 ссылка без доп. полей)")
    public void test1() {
        String jsonAnswer =
                """
            {
               "links" : [ {
                   "id" : 52,
                   "url" : "https://github.com/lirik1254/abTestRepo",
                   "tags" : [ ],
                   "filters" : [ ]
                 } ],
                 "size" : 1
            }""";

        wireMockServer.stubFor(get(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/list");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String returnAnswer =
                """
            Кол-во отслеживаемых ссылок: 1

            Отслеживаемые ссылки:

            1) Ссылка: https://github.com/lirik1254/abTestRepo""";

        assertEquals(
                returnAnswer, sentMessage.getParameters().get("text").toString().replaceAll("\r\n", "\n"));
    }

    @Test
    @DisplayName("Тестирование вывода более чем 1 отслеживаемой ссылки с разным контентов (теги, фильтры)")
    public void test2() {
        String jsonAnswer =
                """
            {
               "links" : [ {
                   "id" : 52,
                   "url" : "https://github.com/lirik1254/abTestRepo",
                   "tags" : [ "aboba" ],
                   "filters" : [ ]
                 },
                  {
                  "id" : 52,
                  "url" : "https://stackoverflow.com/questions/5252525",
                  "tags": [ "test" ],
                   "filters": [ "test:test", "test2:test2" ]
                   }
                 ],
                 "size" : 2
            }""";

        wireMockServer.stubFor(get(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/list");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String returnAnswer =
                """
            Кол-во отслеживаемых ссылок: 2

            Отслеживаемые ссылки:

            1) Ссылка: https://github.com/lirik1254/abTestRepo
            Теги: aboba

            2) Ссылка: https://stackoverflow.com/questions/5252525
            Теги: test
            Фильтры: test:test, test2:test2""";

        assertEquals(
                returnAnswer, sentMessage.getParameters().get("text").toString().replaceAll("\r\n", "\n"));
    }

    @Test
    @DisplayName("Тестирование обработки ошибки 400 от сервера")
    public void test3() {
        String jsonAnswer =
                """
            {
                "description" : "Некорректные параметры52",
                "code" : "400",
                "exceptionName" : "Некорректные параметры",
                "exceptionMessage" : "Параметры некорректныыые",
                "stacktrace" : [ "java.52", "java.25" ]
            }""";

        wireMockServer.stubFor(get(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/list");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        assertEquals("Некорректные параметры52", sentMessage.getParameters().get("text"));
    }
}
