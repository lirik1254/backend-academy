package backend.academy.bot.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.BaseConfigure;
import backend.academy.bot.services.TelegramBotService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DisplayName("Тест команды /joke")
@SpringBootTest
public class JokeCommandTest extends BaseConfigure {
    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Test
    @DisplayName("Тестирование вызова команды /joke")
    public void test1() {
        String answer =
                "Буддийскому монаху приснилось, что он - бабочка, сидящая на лепестках Лотоса Вечности. Но ведь и бабочке могло присниться, что она - монах. Тогда монах стал думать:\n"
                        + "— Так кто же я, монах, которому снится, что он бабочка, или бабочка, которой снится , что она монах?\n"
                        + "Его размышления прервал зычный голос:\n"
                        + "— Петрович, заебал! Полетели собирать пыльцу.";

        wireMockServer.stubFor(get(urlEqualTo("/joke"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain; charset=UTF-8")
                        .withBody(answer)));

        when(message.text()).thenReturn("/joke");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(captor.capture());

        List<SendMessage> messages = captor.getAllValues();
        assertEquals(2, messages.size());

        SendMessage first = messages.get(0);
        assertEquals(123L, first.getParameters().get("chat_id"));
        assertEquals(
                "Генерирую прикол... (Ожидание в среднем 30 секунд)",
                first.getParameters().get("text"));

        SendMessage second = messages.get(1);
        assertEquals(123L, second.getParameters().get("chat_id"));
        assertEquals(answer, second.getParameters().get("text"));
    }

    @Test
    @DisplayName("Ошибка сервера при вызове команды /joke")
    public void test2() {
        wireMockServer.stubFor(get(urlEqualTo("/joke"))
                .withHeader("Tg-Chat-Id", matching("123"))
                .willReturn(aResponse().withStatus(500)));

        when(message.text()).thenReturn("/joke");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot, times(2)).execute(captor.capture());

        List<SendMessage> messages = captor.getAllValues();
        assertEquals(2, messages.size());

        SendMessage first = messages.get(0);
        assertEquals(123L, first.getParameters().get("chat_id"));
        assertEquals(
                "Генерирую прикол... (Ожидание в среднем 30 секунд)",
                first.getParameters().get("text"));

        SendMessage second = messages.get(1);
        assertEquals(123L, second.getParameters().get("chat_id"));
        assertEquals("Ошибка генерации анекдота", second.getParameters().get("text"));
    }
}
