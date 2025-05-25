package backend.academy.bot.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
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

@SpringBootTest
public class TagsCommandTests extends BaseConfigure {

    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Test
    @DisplayName("Тест /tags если теги есть")
    public void test1() {
        String jsonAnswer = """
        [
           "my",
           "tag"
        ]""";

        wireMockServer.stubFor(get(urlEqualTo("/tags/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/tags");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String returnAnswer = """
                my
                tag""";

        assertEquals(
                returnAnswer, sentMessage.getParameters().get("text").toString().replaceAll("\r\n", "\n"));
    }

    @Test
    @DisplayName("Тест /tags если тегов нет")
    public void test2() {
        String jsonAnswer = """
        []""";

        wireMockServer.stubFor(get(urlEqualTo("/tags/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/tags");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String returnAnswer = "У вас пока нет тегов";

        assertEquals(
                returnAnswer, sentMessage.getParameters().get("text").toString().replaceAll("\r\n", "\n"));
    }

    @Test
    @DisplayName("Некорректный ответ от сервера")
    public void test3() {
        String jsonAnswer = """
        lorem ipso  jfjf""";

        wireMockServer.stubFor(get(urlEqualTo("/tags/123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonAnswer)));

        when(message.text()).thenReturn("/tags");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();

        assertEquals(123L, sentMessage.getParameters().get("chat_id"));

        String returnAnswer = "Ошибка";

        assertEquals(
                returnAnswer, sentMessage.getParameters().get("text").toString().replaceAll("\r\n", "\n"));
    }
}
