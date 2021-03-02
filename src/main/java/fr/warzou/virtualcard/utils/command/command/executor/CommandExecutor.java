package fr.warzou.virtualcard.utils.command.command.executor;

import fr.warzou.virtualcard.utils.command.commandsystem.Command;

public interface CommandExecutor {

    boolean execute(Command command, String[] arguments);

}
