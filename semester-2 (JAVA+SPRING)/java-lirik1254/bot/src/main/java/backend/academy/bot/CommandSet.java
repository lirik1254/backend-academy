package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandSet {
    private final TelegramBot bot;

    @EventListener(ContextRefreshedEvent.class)
    public void initBotCommands() {
        BotCommand[] commands = {
            new BotCommand("/start", "Запуск бота + регистрация"),
            new BotCommand("/help", "Список команд + описание"),
            new BotCommand("/list", "Список отслеживаемых ссылок"),
            new BotCommand("/track", "Начать отслеживание"),
            new BotCommand("/untrack", "Прекратить отслеживание"),
            new BotCommand("/tags", "Вывести все теги"),
            new BotCommand("/linktags", "Вывести все ссылки по тегам"),
            new BotCommand("/timesettings", "Настроить время отправки уведомлений"),
            new BotCommand("/joke", "Получить шутку по контенту случайной отслеживаемой ссылки")
        };

        try {
            bot.execute(new SetMyCommands(commands));
            log.info("Команды успешно установлены");
        } catch (Exception e) {
            log.error("Ошибка при установке команд: ", e);
        }
    }
}
