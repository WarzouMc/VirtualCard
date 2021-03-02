package fr.warzou.virtualcard.utils.command.commandsystem;

import java.util.*;

public class CommandRegister {

    private final List<Command> commands;

    public CommandRegister() {
        this.commands = new ArrayList<>();
    }

    public void addCommand(Command command) {
        if (safeCommandProvider(command.getName()).isPresent())
            throw new IllegalArgumentException("Command '" + command.getName() + "' is already registered");
        this.commands.add(command);
    }

    public Command getCommand(String name) {
        Optional<Command> command = safeCommandProvider(name);
        return command.orElse(null);
    }

    public List<Command> getCommands() {
        return Collections.unmodifiableList(this.commands);
    }

    public Optional<Command> safeCommandProvider(String name) {
        return this.commands.stream()
                .filter(command -> command.getName().equalsIgnoreCase(name) || Arrays.asList(command.getAlias()).contains(name.toUpperCase()))
                .findFirst();
    }
}
