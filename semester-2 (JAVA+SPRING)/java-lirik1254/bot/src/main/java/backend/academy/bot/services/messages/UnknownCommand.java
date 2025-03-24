package backend.academy.bot.services.messages;

import backend.academy.bot.utils.BotMessages;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UnknownCommand implements Command {
    private final TelegramBot bot;

    @Override
    public void execute(Long chatId, String message) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .setMessage("Выполняется неизвестная команда")
                .log();
        bot.execute(new SendMessage(chatId, BotMessages.WRONG_COMMAND));
    }

    @Override
    public String getName() {
        return "";
    }
}
