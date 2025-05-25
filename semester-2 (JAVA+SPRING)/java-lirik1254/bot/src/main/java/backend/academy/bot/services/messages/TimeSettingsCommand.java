package backend.academy.bot.services.messages;

import static general.LogMessages.CHAT_ID_STRING;

import backend.academy.bot.clients.TimeSettingsClient;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import dto.Settings;
import dto.TimeSettingsDTO;
import java.time.LocalTime;
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
public class TimeSettingsCommand implements Command {
    private final TelegramBot bot;
    private final TimeSettingsClient timeSettingsClient;

    private final Map<Long, State> userStates = new ConcurrentHashMap<>();
    private final Map<Long, Settings> userSettings = new ConcurrentHashMap<>();

    String startMessage =
            """
        Сейчас ваши настройки следующие:
        %s

        Выберите, когда вам отправлять уведомления:

        1. Сразу
        2. Раз в сутки

        Введите либо 1, либо 2""";

    String cancelMessage = "Вы вышли из выбора способа отправки уведомлений";

    @Override
    public void execute(Long chatId, String message) {
        log.atInfo()
                .addKeyValue(CHAT_ID_STRING, chatId)
                .setMessage("Выполняется команда /timesettings")
                .log();

        State currentState = userStates.getOrDefault(chatId, State.START);

        switch (currentState) {
            case START -> startHandle(chatId);
            case WAITING_FOR_SETTING_CHOOSE -> waitingForSettingChooseHandle(chatId, message);
            case WAITING_FOR_TIME_CHOOSE -> waitingForTimeChoose(chatId, message);
            default -> {}
        }
    }

    @Override
    public String getName() {
        return CommandName.TIMESETTINGS.commandName();
    }

    public void startHandle(Long chatId) {
        String message = timeSettingsClient.getTimeSettings(chatId);
        bot.execute(new SendMessage(chatId, String.format(startMessage, message)));
        userStates.put(chatId, State.WAITING_FOR_SETTING_CHOOSE);
    }

    private void waitingForSettingChooseHandle(Long chatId, String message) {
        switch (message) {
            case "/stop" -> {
                bot.execute(new SendMessage(chatId, cancelMessage));
                userStates.put(chatId, State.START);
            }
            case "1" -> {
                userSettings.put(chatId, Settings.IMMEDIATELY);
                userStates.put(chatId, State.START);
                String response =
                        timeSettingsClient.saveTimeSettings(chatId, new TimeSettingsDTO(Settings.IMMEDIATELY, null));
                bot.execute(new SendMessage(chatId, response));
            }
            case "2" -> {
                bot.execute(new SendMessage(chatId, "Выберите время (например, 10:00)"));
                userSettings.put(chatId, Settings.BY_TIME);
                userStates.put(chatId, State.WAITING_FOR_TIME_CHOOSE);
            }
            default -> bot.execute(new SendMessage(chatId, "Введите либо 1, либо 2, либо /stop"));
        }
    }

    private void waitingForTimeChoose(Long chatId, String message) {
        if (message.equals("/stop")) {
            bot.execute(new SendMessage(chatId, cancelMessage));
            userStates.put(chatId, State.START);
        } else {
            try {
                LocalTime time = LocalTime.parse(message);
                userStates.put(chatId, State.START);
                String response =
                        timeSettingsClient.saveTimeSettings(chatId, new TimeSettingsDTO(Settings.BY_TIME, time));
                bot.execute(new SendMessage(chatId, response));
            } catch (Exception e) {
                bot.execute(new SendMessage(chatId, "Введите время в формате hh:mm, либо введите введите /stop"));
            }
        }
    }
}
