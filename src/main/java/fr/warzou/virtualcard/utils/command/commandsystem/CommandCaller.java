package fr.warzou.virtualcard.utils.command.commandsystem;

import fr.warzou.virtualcard.api.Card;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandCaller {

    private final Card api;
    private final CommandRegister commandRegister;

    public CommandCaller(@NotNull Card api) {
        this.api = api;
        this.commandRegister = this.api.getCommandRegister();
    }

    public boolean execute(@NotNull String rawCommand) {
        if (rawCommand.isEmpty())
            return false;

        rawCommand = rawCommand.substring(1);
        String[] split = rawCommand.split(" ");
        String[] arguments;
        if (split.length == 1)
            arguments = new String[0];
        else {
            List<String> list = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
            arguments = list.toArray(new String[split.length - 1]);
        }
        return execute(split[0], arguments);
    }

    private boolean execute(String root, String[] arguments) {
        if (!isRegisteredCommand(root))
            return false;
        Optional<Command> commandOptional = parse(root);
        if (!commandOptional.isPresent())
            return false;
        Command command = commandOptional.get();
        return command.executor.execute(command, arguments);
    }

    private Optional<Command> parse(String name) {
        return this.commandRegister.safeCommandProvider(name);
    }

    private boolean isRegisteredCommand(String commandName) {
        return this.commandRegister.safeCommandProvider(commandName).isPresent();
    }
}
