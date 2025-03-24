package backend.academy.bot.services.messages;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandContainer {
    private final Map<String, Command> commandMap;
    private final UnknownCommand unknownCommand;

    @Autowired
    public CommandContainer(List<Command> commands, UnknownCommand unknownCommand) {
        this.unknownCommand = unknownCommand;
        commandMap = commands.stream().collect(Collectors.toMap(Command::getName, Function.identity()));
    }

    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, unknownCommand);
    }
}
