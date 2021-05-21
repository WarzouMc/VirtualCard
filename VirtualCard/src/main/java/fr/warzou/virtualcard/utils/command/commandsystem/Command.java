package fr.warzou.virtualcard.utils.command.commandsystem;

import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;

/**
 * Juste a command.
 * @author Warzou
 * @version 0.0.2
 */
public abstract class Command {

    /**
     * {@link CommandExecutor} of this command.
     */
    protected CommandExecutor executor;

    /**
     * Returns command name.
     * @return command name
     */
    public abstract String getName();

    /**
     * Returns command description.
     * @return command description
     */
    public abstract String getDescription();

    /**
     * Returns command help message.
     * @return command help
     */
    public abstract String getHelpMessage();

    /**
     * Returns command alias.
     * @return command alias
     */
    public abstract String[] getAlias();

    /**
     * Returns the source of this command.
     * <p>Return plugin name where this command is init.</p>
     * @return source of command
     */
    public abstract String source();

    /**
     * Replace old {@link CommandExecutor}.
     * @param executor new {@link CommandExecutor}
     */
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

}
