package backend.academy.bot.services.messages;

import backend.academy.bot.clients.TagsClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class TagsCommand implements Command {
    private final TagsClient tagsClient;
    private final TelegramBot telegramBot;

    @Override
    public void execute(Long chatId, String message) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .setMessage("Выполняется команда /tags")
                .log();
        telegramBot.execute(new SendMessage(chatId, tagsClient.getAllTags(chatId)));
    }

    @Override
    public String getName() {
        return CommandName.TAGS.commandName();
    }
}
