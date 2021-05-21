package fr.warzou.virtualcard.utils.command.commandsystem;

import java.util.*;

/**
 * Register commands.
 * @author Warzou
 * @version 0.0.2
 */
public class CommandRegister {

    /**
     * Commands list
     */
    private final List<Command> commands;

    public CommandRegister() {
        this.commands = new ArrayList<>();
    }

    /**
     * Add a command.
     * @param command target command
     */
    public void addCommand(Command command) {
        if (safeCommandProvider(command.getName()).isPresent()) {
            System.err.println("The command '" + command.getName() + "' already exist !");
            return;
        }
        this.commands.add(command);
    }

    /**
     * Returns a {@link Command} from its name.
     * @param name command name
     * @return command from name
     */
    public Command getCommand(String name) {
        Optional<Command> command = safeCommandProvider(name);
        return command.orElse(null);
    }

    /**
     * Returns list of every registered {@link Command}.
     * @return command list
     */
    public List<Command> getCommands() {
        return new ArrayList<>(this.commands);
    }

    /**
     * Return a {@link Command} from its name.
     * @param name command name
     * @return Empty optional command if any command has a name like that and non-empty optional with a {@link Command}.
     */
    public Optional<Command> safeCommandProvider(String name) {
        return this.commands.stream()
                .filter(command -> command.getName().equalsIgnoreCase(name) || Arrays.asList(command.getAlias()).contains(name.toUpperCase()))
                .findFirst();
    }
}
