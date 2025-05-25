package backend.academy.bot.services.messages;

import backend.academy.bot.clients.JokeClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JokeCommand implements Command {
    private final JokeClient jokeClient;
    private final TelegramBot telegramBot;
    private static final String JOKE_MESSAGE = "Генерирую прикол... (Ожидание в среднем 30 секунд)";

    @Override
    public void execute(Long chatId, String message) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .setMessage("Выполняется команда /joke")
                .log();
        telegramBot.execute(new SendMessage(chatId, JOKE_MESSAGE));
        telegramBot.execute(new SendMessage(chatId, jokeClient.getJoke(chatId)));
    }

    @Override
    public String getName() {
        return CommandName.JOKE.commandName();
    }
}
