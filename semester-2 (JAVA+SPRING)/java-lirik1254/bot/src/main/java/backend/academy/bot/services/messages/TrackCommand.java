package backend.academy.bot.services.messages;

import static general.LogMessages.CHAT_ID_STRING;

import backend.academy.bot.clients.TrackClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import general.RegexCheck;
import java.util.ArrayList;
import java.util.Arrays;
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
@SuppressWarnings("MissingSwitchDefault")
public class TrackCommand implements Command {
    public static String SKIP = "/skip";

    private final Map<Long, State> userStates = new ConcurrentHashMap<>();
    private final Map<Long, String> userUrl = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, List<String>>> linkTags = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, List<String>>> linkFilters = new ConcurrentHashMap<>();

    private final TelegramBot bot;
    private final TrackClient trackClient;
    private final RegexCheck regexCheck;

    @Override
    public void execute(Long chatId, String message) {
        log.atInfo()
                .addKeyValue(CHAT_ID_STRING, chatId)
                .setMessage("Выполняется команда /track")
                .log();
        State currentState = userStates.getOrDefault(chatId, State.START);

        switch (currentState) {
            case START -> startHandle(chatId);
            case WAITING_FOR_URL -> waitingForUrlHandle(chatId, message);
            case WAITING_FOR_TAGS -> waitingForTags(chatId, message);
            case WAITING_FOR_FILTERS -> waitingForFilters(chatId, message);
            default -> {}
        }
    }

    @Override
    public String getName() {
        return CommandName.TRACK.commandName();
    }

    private void startHandle(Long chatId) {
        log.atInfo()
                .addKeyValue(CHAT_ID_STRING, chatId)
                .setMessage("Просят ввести url")
                .log();
        bot.execute(new SendMessage(chatId, "Введите URL для отслеживания (см. /help)"));
        userStates.put(chatId, State.WAITING_FOR_URL);
    }

    private void waitingForUrlHandle(Long chatId, String message) {
        if (message.trim().equals("/stop")) {
            log.atInfo()
                    .addKeyValue(CHAT_ID_STRING, chatId)
                    .setMessage("Пользователь ввёл /stop")
                    .log();
            userStates.put(chatId, State.START);
            bot.execute(new SendMessage(chatId, "Вы вышли из меню ввода ссылки"));
            return;
        }
        if (regexCheck.checkApi(message)) {
            log.atInfo()
                    .addKeyValue(CHAT_ID_STRING, chatId)
                    .setMessage("Пользователя просят ввести теги для чата")
                    .log();
            userUrl.put(chatId, message);
            userStates.put(chatId, State.WAITING_FOR_TAGS);
            bot.execute(new SendMessage(chatId, "Введите теги (опционально).\nЕсли теги не нужны - введите /skip"));
        } else {
            log.atInfo()
                    .addKeyValue(CHAT_ID_STRING, chatId)
                    .setMessage("Пользователь некорректно ввёл ссылка для чата")
                    .log();
            bot.execute(new SendMessage(chatId, "Некорректно введена ссылка, введите заново, либо введите /stop"));
        }
    }

    private void waitingForTags(Long chatId, String message) {
        if (!message.equals(SKIP)) {
            log.atInfo()
                    .addKeyValue(CHAT_ID_STRING, chatId)
                    .setMessage("Пользователь ввёл теги")
                    .log();
            linkTags.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>())
                    .put(userUrl.get(chatId), new ArrayList<>(Arrays.asList(message.split(" "))));
        }
        userStates.put(chatId, State.WAITING_FOR_FILTERS);
        bot.execute(new SendMessage(
                chatId,
                "Введите фильтры (опционально, например, user:dummy)\n" + "Если фильтры не нужны - введите /skip"));
    }

    private void waitingForFilters(Long chatId, String message) {
        boolean isSkip = message.equals(SKIP);
        boolean isValidFilter = regexCheck.checkFilter(message);

        if (isSkip || isValidFilter) {
            userStates.put(chatId, State.START);

            Map<String, List<String>> urlTags = linkTags.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>());
            ArrayList<String> tags =
                    (ArrayList<String>) urlTags.computeIfAbsent(userUrl.get(chatId), k -> new ArrayList<>());

            Map<String, List<String>> urlFilters = linkFilters.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>());
            List<String> filters;

            if (isSkip) {
                filters = urlFilters.computeIfAbsent(userUrl.get(chatId), k -> new ArrayList<>());
            } else {
                filters = new ArrayList<>(Arrays.asList(message.split(" ")));
                urlFilters.put(userUrl.get(chatId), filters);
            }
            log.atInfo()
                    .addKeyValue(CHAT_ID_STRING, chatId)
                    .setMessage("Пользователь ввёл фильтры")
                    .log();
            bot.execute(new SendMessage(chatId, trackClient.trackLink(chatId, userUrl.get(chatId), tags, filters)));
        } else {
            log.atInfo()
                    .addKeyValue(CHAT_ID_STRING, chatId)
                    .setMessage("Пользователь некорректно ввёл фильтры")
                    .log();
            bot.execute(new SendMessage(chatId, "Введите фильтры в формате filter:filter"));
        }
    }
}
