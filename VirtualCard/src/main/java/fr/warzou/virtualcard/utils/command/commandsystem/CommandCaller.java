package fr.warzou.virtualcard.utils.command.commandsystem;

import fr.warzou.virtualcard.api.Card;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Execute a command.
 * @author Warzou
 * @version 0.0.2
 */
public class CommandCaller {

    /**
     * Command register
     */
    private final CommandRegister commandRegister;

    /**
     * Create a new instance of {@link CommandCaller}.
     * @param card card
     */
    public CommandCaller(@NotNull Card card) {
        this.commandRegister = card.getCommandRegister();
    }

    /**
     * Execute a command.
     * @param rawCommand command raw name (means with arguments)
     * @return true if success
     */
    public boolean execute(String rawCommand) {
        if (rawCommand == null || rawCommand.isEmpty())
            return false;

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

    /**
     * Execute a command.
     * @param root command name
     * @param arguments command arguments
     * @return true if success
     */
    private boolean execute(String root, String[] arguments) {
        if (!isRegisteredCommand(root))
            return false;
        Optional<Command> commandOptional = parse(root);
        if (!commandOptional.isPresent())
            return false;
        Command command = commandOptional.get();
        if (command.executor == null) {
            NullPointerException nullPointerException = new NullPointerException("Command executor of " +
                    command.getName() + " command doesn't exist !");
            nullPointerException.printStackTrace();
            return false;
        }
        return command.executor.execute(command, arguments);
    }

    /**
     * Return a {@link Command} from its name.
     * @see CommandRegister#safeCommandProvider(String)
     * @param name command name
     * @return Empty optional command if any command has a name like that and non-empty optional with a Command.
     */
    private Optional<Command> parse(String name) {
        return this.commandRegister.safeCommandProvider(name);
    }

    /**
     * Check if a command is registered.
     * @param commandName command name
     * @return true if the command is registered
     */
    private boolean isRegisteredCommand(String commandName) {
        return this.commandRegister.safeCommandProvider(commandName).isPresent();
    }
}
