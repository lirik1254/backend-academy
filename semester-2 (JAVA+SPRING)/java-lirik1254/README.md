![Build](https://github.com/central-university-dev/backend-academy-2025-spring-template/actions/workflows/build.yaml/badge.svg)

# Link Tracker

<!-- этот файл можно и нужно менять -->

Проект сделан в рамках курса Академия Бэкенда.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`.
Для запуска базы данных и применения миграций пропишите:

```shell
docker-compose up -d
```

С запущенным Docker

Присутствует опциональная зависимость на `Kafka`.

Для дополнительной справки: [HELP.md](./HELP.md)

Для запуска приложения необходимо запустить bot и scrapper модуль

```shell
cd bot
```

и введите

```shell
mvn spring-boot:run
```

Аналогично для scrapper.

ВАЖНО! Для работы приложения требуется определить в системных переменных:

GITHUB_TOKEN - токен для доступа к api github

SO_TOKEN_KEY - ключ stackoverflow api для доступа к api stackoverflow

SO_ACCESS_TOKEN - токен stackoverflow api для доступа к api stackoverflow

TELEGRAM_TOKEN - токен телеграм бота

DB_USERNAME - пользователь БД (произвольное значение)

DB_PASSWORD - пароль от БД (произвольное значение)
