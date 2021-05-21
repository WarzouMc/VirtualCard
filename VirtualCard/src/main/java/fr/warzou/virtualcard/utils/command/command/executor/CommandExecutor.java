package fr.warzou.virtualcard.utils.command.command.executor;

import fr.warzou.virtualcard.utils.command.commandsystem.Command;

/**
 * Command executor interface.
 * <p>When a {@link Command} is trigger its executor is {@link CommandExecutor#execute(Command, String[])} too.</p>
 * @author Warzou
 * @version 0.0.1
 */
public interface CommandExecutor {

    /**
     * Execute a command.
     * @param command target {@link Command}
     * @param arguments command arguments
     * @return true if execute is a success
     */
    boolean execute(Command command, String[] arguments);

}
