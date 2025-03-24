package backend.academy.bot.services.messages;

import static general.LogMessages.CHAT_ID_STRING;

import backend.academy.bot.clients.TrackClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
@SuppressWarnings({"ReturnCount", "MissingSwitchDefault"})
public class UntrackCommand implements Command {
    private final TelegramBot bot;
    private final TrackClient trackClient;

    private final Map<Long, State> userStates = new ConcurrentHashMap<>();

    @Override
    public void execute(Long chatId, String message) {
        State currentState = userStates.getOrDefault(chatId, State.START);
        log.atInfo()
                .addKeyValue(CHAT_ID_STRING, chatId)
                .setMessage("Выполняется команда /untrack")
                .log();
        switch (currentState) {
            case START -> {
                log.atInfo()
                        .addKeyValue(CHAT_ID_STRING, chatId)
                        .setMessage("Пользователя просят ввести url для прекращения отслеживания")
                        .log();
                bot.execute(new SendMessage(chatId, "Введите URL для прекращения отслеживания (см. /help)"));
                userStates.put(chatId, State.WAITING_FOR_URL);
            }
            case WAITING_FOR_URL -> {
                if (message.trim().equals("/stop")) {
                    log.atInfo()
                            .addKeyValue(CHAT_ID_STRING, chatId)
                            .setMessage("Пользователь вышел из меню ввода ссылки для прекращения отслеживания")
                            .log();
                    userStates.put(chatId, State.START);
                    bot.execute(new SendMessage(chatId, "Вы вышли из меню ввода ссылки"));
                    return;
                }
                String retMessage = trackClient.unTrackLink(chatId, message);
                if (retMessage.equals("Нет такой ссылки")) {
                    log.atInfo()
                            .addKeyValue("chatId", chatId)
                            .setMessage("Пользователь ввёл некорректную ссылку для прекращения отслеживания")
                            .log();
                    bot.execute(new SendMessage(chatId, "Нет такой ссылки. Введите заново, либо введите /stop"));
                    return;
                }
                if (!retMessage.equals("Ошибка")) {
                    log.atInfo()
                            .addKeyValue(CHAT_ID_STRING, chatId)
                            .addKeyValue("userMessage", message)
                            .setMessage("Отслеживание ссылки прекращено")
                            .log();
                }
                userStates.put(chatId, State.START);
                bot.execute(new SendMessage(chatId, retMessage));
            }
            default -> {}
        }
    }

    @Override
    public String getName() {
        return CommandName.UNTRACK.commandName();
    }
}
