package backend.academy.bot.services.messages;

import lombok.Getter;

@Getter
public enum CommandName {
    START("/start"),
    LIST("/list"),
    HELP("/help"),
    TRACK("/track"),
    UNTRACK("/untrack"),
    TAGS("/tags"),
    LINKTAGS("/linktags"),
    TIMESETTINGS("/timesettings"),
    JOKE("/joke");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }
}
