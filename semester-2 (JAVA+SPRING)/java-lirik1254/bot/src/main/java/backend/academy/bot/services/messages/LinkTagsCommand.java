package backend.academy.bot.services.messages;

import static general.LogMessages.CHAT_ID_STRING;

import backend.academy.bot.clients.TagLinksClient;
import backend.academy.bot.clients.TagsClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
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
public class LinkTagsCommand implements Command {
    private final TelegramBot bot;
    private final TagLinksClient tagLinksClient;
    private final TagsClient tagsClient;

    private final Map<Long, State> userStates = new ConcurrentHashMap<>();

    @Override
    public void execute(Long chatId, String message) {
        State currentState = userStates.getOrDefault(chatId, State.START);
        log.atInfo()
                .addKeyValue(CHAT_ID_STRING, chatId)
                .setMessage("Выполняется команда /linkTags")
                .log();
        switch (currentState) {
            case START -> {
                log.atInfo()
                        .addKeyValue(CHAT_ID_STRING, chatId)
                        .setMessage("Пользователя просят ввести url для прекращения отслеживания")
                        .log();
                if (tagsClient.getAllTags(chatId).equals("У вас пока нет тегов")) {
                    bot.execute(new SendMessage(chatId, "У вас нет тегов"));
                    userStates.put(chatId, State.START);
                } else {
                    bot.execute(
                            new SendMessage(chatId, "Введите теги через пробел, по которым вы хотите вывести ссылки"));
                    userStates.put(chatId, State.WAITING_FOR_LINK_TAGS);
                }
            }
            case WAITING_FOR_LINK_TAGS -> {
                if (message.trim().equals("/stop")) {
                    log.atInfo()
                            .addKeyValue(CHAT_ID_STRING, chatId)
                            .setMessage("Пользователь вышел из меню ввода тегов")
                            .log();
                    userStates.put(chatId, State.START);
                    bot.execute(new SendMessage(chatId, "Вы вышли из меню ввода тегов"));
                    return;
                }
                String retMessage = tagLinksClient.getAllTagLinks(chatId, List.of(message.split(" ")));
                log.atInfo()
                        .addKeyValue(CHAT_ID_STRING, chatId)
                        .addKeyValue("userMessage", message)
                        .setMessage("Отслеживание ссылки прекращено")
                        .log();
                userStates.put(chatId, State.START);
                bot.execute(new SendMessage(chatId, retMessage));
            }
            default -> {}
        }
    }

    @Override
    public String getName() {
        return CommandName.LINKTAGS.commandName();
    }
}
