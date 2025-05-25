package backend.academy.scrapper.services;

import backend.academy.scrapper.clients.AIClient;
import backend.academy.scrapper.services.interfaces.ContentService;
import dto.ContentDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIService {
    private final AIClient aiClient;
    private final ContentService contentService;
    private static final String message =
            """
        Ты рассказываешь анекдоты, сформированные по определённым сообщениям ответов/комментариев с
        stackoveflow или по определённым сообщениям ISSUE в репозитории github.
        Сейчас ты получишь данные об ответах пользователя или о сформированных вопросах. Постарайся написать анекдот
        по этой теме. Анекдот должен быть примерно в 15-50 слов, обязательно на русском языке. И в тему. И постироничный
        И абстрактный и смешной. Вот пример прикола:" +
        Бежала лиса по лесу, а из кустов:
        — Ку-ка-ре-ку....
        Лиса — в кусты. Возня, шорох с придыханием. Выходит волк, застегивая ширинку и поправляя штаны:
        — Нахуя мне штаны? Я же волк
        Прикол необязательно должен быть таким, который я скинул выше, можешь ориентироваться на него, можешь придумать нечто другое.
        Теперь скидываю тебе сообщения, по которым нужно сгенерировать прикол""";

    public String getJoke(Long chatId) {
        List<ContentDTO> contentByRandomUrl = contentService.getContentByRandomUrl(chatId);
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(message).append("\n\n");
        contentByRandomUrl.forEach(content -> userMessage
                .append("Тип: ")
                .append(content.type().toString())
                .append("\n")
                .append("Заголовок вопроса: ")
                .append(content.title())
                .append("\n")
                .append("Автор: ")
                .append(content.userName())
                .append("\n")
                .append("Ответ: ")
                .append(content.answer())
                .append("\n")
                .append("Дата: ")
                .append(content.creationTime())
                .append("\n\n"));
        return aiClient.createChatCompletion(userMessage.toString());
    }
}
