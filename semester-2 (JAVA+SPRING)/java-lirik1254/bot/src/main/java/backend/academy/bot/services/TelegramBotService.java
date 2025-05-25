package backend.academy.bot.services;

import backend.academy.bot.MessageMetrics;
import backend.academy.bot.services.messages.CommandContainer;
import backend.academy.bot.services.messages.LinkTagsCommand;
import backend.academy.bot.services.messages.State;
import backend.academy.bot.services.messages.TimeSettingsCommand;
import backend.academy.bot.services.messages.TrackCommand;
import backend.academy.bot.services.messages.UntrackCommand;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("ReturnCount")
public class TelegramBotService {

    private final TelegramBot bot;
    private final CommandContainer commandContainer;
    private final TrackCommand trackCommand;
    private final UntrackCommand untrackCommand;
    private final LinkTagsCommand linkTagsCommand;
    private final MessageMetrics messageService;
    private final TimeSettingsCommand timeSettingsCommand;

    @PostConstruct
    public void startListening() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                messageService.incrementMessage();
                if (update.message() != null && update.message().text() != null) {
                    log.atInfo()
                            .addKeyValue("chatId", update.message().chat().id())
                            .addKeyValue("userMessage", update.message().text())
                            .setMessage("Пришло сообщение")
                            .log();
                    CompletableFuture.runAsync(() -> handleMessage(update), Executors.newFixedThreadPool(20));
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void handleMessage(Update update) {
        Long chatId = update.message().chat().id();
        String messageText = update.message().text();

        State currentTrackState = trackCommand.userStates().getOrDefault(chatId, State.START);
        State currentUntrackState = untrackCommand.userStates().getOrDefault(chatId, State.START);
        State currentLinkTagsState = linkTagsCommand.userStates().getOrDefault(chatId, State.START);
        State currentSettingState = timeSettingsCommand.userStates().getOrDefault(chatId, State.START);

        if (currentTrackState != State.START) {
            trackCommand.execute(chatId, messageText);
            return;
        }

        if (currentUntrackState != State.START) {
            untrackCommand.execute(chatId, messageText);
            return;
        }

        if (currentLinkTagsState != State.START) {
            linkTagsCommand.execute(chatId, messageText);
            return;
        }

        if (currentSettingState != State.START) {
            timeSettingsCommand.execute(chatId, messageText);
            return;
        }

        if (messageText != null) {
            commandContainer.retrieveCommand(messageText.trim()).execute(chatId, messageText);
        }
    }
}
