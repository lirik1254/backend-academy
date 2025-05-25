package backend.academy.bot.services.messages;

import backend.academy.bot.clients.TrackClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ListCommand implements Command {
    private final TelegramBot bot;
    private final TrackClient trackClient;

    @Override
    public void execute(Long chatId, String message) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .setMessage("Выполняется команда /list")
                .log();
        String retMessage = "";
        try {
            retMessage = trackClient.getTrackLinks(chatId);
        } catch (Exception e) {
            retMessage = "Ошибка";
        }
        bot.execute(new SendMessage(chatId, retMessage));
    }

    @Override
    public String getName() {
        return CommandName.LIST.commandName();
    }
}
