package backend.academy.bot.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.BaseConfigure;
import backend.academy.bot.services.TelegramBotService;
import backend.academy.bot.utils.BotMessages;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class UnknownCommandTest extends BaseConfigure {
    @MockitoBean
    private TelegramBot telegramBot;

    @Autowired
    private TelegramBotService telegramBotService;

    @Test
    @DisplayName("Тестирование вызова неизвестной команды")
    public void test() {
        when(message.text()).thenReturn("/wrongCommand");

        telegramBotService.handleMessage(update);

        ArgumentCaptor<SendMessage> captor = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramBot).execute(captor.capture());

        SendMessage sentMessage = captor.getValue();
        assertEquals(123L, sentMessage.getParameters().get("chat_id"));
        assertEquals(BotMessages.WRONG_COMMAND, sentMessage.getParameters().get("text"));
    }
}
