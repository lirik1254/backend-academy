https://github.com/user-attachments/assets/e1b98406-84bc-40d4-b8a3-6bed98199328

# Link Tracker

<!-- этот файл можно и нужно менять -->

Проект сделан в рамках курса Академия Бэкенда.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для запуска системы введите:

```shell
docker-compose up -d --build
```

С запущенным Docker

Для дополнительной справки: [HELP.md](./HELP.md)

ВАЖНО! Для работы приложения требуется определить в системных переменных:

GITHUB_TOKEN - токен для доступа к api github

SO_TOKEN_KEY - ключ stackoverflow api для доступа к api stackoverflow

SO_ACCESS_TOKEN - токен stackoverflow api для доступа к api stackoverflow

TELEGRAM_TOKEN - токен телеграм бота

DB_USERNAME - пользователь БД (произвольное значение)

DB_PASSWORD - пароль от БД (произвольное значение)

HYPERBOLIC_API_KEY - ключ от hyperbolic.xyz (недоступен на территории РФ)


Интересные особенности реализации: 
* Отправка обновлений настраивается через конфигурацию (HTTP/kafka)
* При ошибке отправки обновления посредством http происходит фолбек на кафку
* Способ работы с базой данных настраивается через конфигурацию (ORM/SQL)
* Присутствует rate limiter по ip, retry, circuit breaker, read-timeout, connect-timeout. Настраиваются через конфигурацию
* Подключен prometheus + grafana, присутствуют кастомные метрики:
    * Кол-во сообщений от пользователя
    * Кол-во активных ссылок в БД по типу
    * Скорость скрапинга данных по типу
* Обновления обрабатывается в разных потоках
* Батчовая выгрузка и обработка данных
* Используется паттерн "Команда" для гибкого добавления новых команд
