package backend.academy.bot.services.messages;

public interface Command {
    void execute(Long chatId, String message);

    String getName();
}
