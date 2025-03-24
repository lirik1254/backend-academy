package backend.academy.bot.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("RegexpSingleline")
public class BotMessages {
    public static final String HELP_MESSAGE =
            """
        Доступные команды:

        /start - Регистрация

        /help - Подробно поясняет что делает каждая команда

        /track - Начинает отслеживание ссылки.

        Всего доступны отслеживание 2 ссылок:
        1 - Отслеживания изменения репозитория GitHub
        2 - Отслеживание изменения кол-ва ответов на вопрос StackOverflow

        Ссылки должны быть в формате:
        Для GitHub - https://github.com/<owner>/<repo>

        <owner> - Владелец репозитория (любые символы длины от 1 до 39)
        <repo> - Репозиторий (любые символы длины от 1 до 39)

        Для StackOverflow - https://<site>/questions/<question_id>

        <question_id> - id вопроса (1-10 чисел)
        <site> - ru.stackoverflow.com или stackoverflow.com

        Для ввода тегов - любые слова/символы через пробел
        Для ввода фильтров - фильтры в формате filter:filter

        /untrack - Прекращает отслеживание какой-либо ссылки

        /list - Показывает список отслеживаемых ссылок (список ссылок, полученных при /track)
        /linkTag - Выводит ссылки по тегам
        /tags - Выводит все теги пользователя""";

    public static final String WRONG_COMMAND = "Неизвестная команда";
}
