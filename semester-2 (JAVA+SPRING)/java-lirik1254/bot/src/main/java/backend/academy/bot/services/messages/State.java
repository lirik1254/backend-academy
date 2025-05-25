package backend.academy.bot.services.messages;

public enum State {
    START,
    WAITING_FOR_URL,
    WAITING_FOR_TAGS,
    WAITING_FOR_FILTERS,
    WAITING_FOR_LINK_TAGS,
    WAITING_FOR_TIME_CHOOSE,
    WAITING_FOR_SETTING_CHOOSE
}
