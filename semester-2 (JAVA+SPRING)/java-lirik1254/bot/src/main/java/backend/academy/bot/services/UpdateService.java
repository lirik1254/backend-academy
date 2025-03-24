package backend.academy.bot.services;

import static general.LogMessages.CHAT_ID_STRING;
import static general.LogMessages.URL;

import backend.academy.bot.BotConfig;
import backend.academy.bot.utils.UpdateServiceUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import dto.ContentDTO;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private final UpdateServiceUtils updateServiceUtils;
    private final TelegramBot bot;
    private final BotConfig botConfig;

    public void update(List<Long> tgChatIds, String url, ContentDTO contentDTO) {
        String time = updateServiceUtils.dateConverter(contentDTO.creationTime());
        String answer = updateServiceUtils.answerDelimitation(contentDTO.answer());

        try {
            tgChatIds.forEach(id -> {
                log.atInfo()
                        .addKeyValue(CHAT_ID_STRING, id)
                        .addKeyValue(URL, url)
                        .setMessage("Отправлено обновление")
                        .log();
                bot.execute(new SendMessage(
                        id,
                        String.format(
                                "Пришло уведомление по url %s%n" + "Тип: %s%n"
                                        + "Текст темы: %s%n"
                                        + "Имя пользователя: %s%n"
                                        + "Время создания: %s%n"
                                        + "Превью: %s",
                                url, contentDTO.type(), contentDTO.title(), contentDTO.userName(), time, answer)));
            });
        } catch (Exception e) {
            log.atError()
                    .addKeyValue(CHAT_ID_STRING, tgChatIds.getFirst())
                    .addKeyValue(URL, url)
                    .setMessage("Некорректные параметры запроса при отправке обновления")
                    .log();
            throw new RuntimeException("Некорректные параметры запроса");
        }
    }

}
