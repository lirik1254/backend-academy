package backend.academy.bot.services.messages;

import backend.academy.bot.clients.RegistrationClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class StartCommand implements Command {
    private final RegistrationClient registrationClient;
    private final TelegramBot bot;

    @Override
    public void execute(Long chatId, String message) {
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .setMessage("Выполняется команда /start")
                .log();
        String returnMessage = "";
        try {
            returnMessage = registrationClient.registerUser(chatId);
        } catch (Exception e) {
            returnMessage = "Ошибка";
        }
        bot.execute(new SendMessage(chatId, returnMessage));
    }

    @Override
    public String getName() {
        return CommandName.START.commandName();
    }
}
