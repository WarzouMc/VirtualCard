package fr.warzou.virtualcard.utils.command.commandsystem;

import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;

public abstract class Command {

    protected CommandExecutor executor;

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getHelpMessage();

    public abstract String[] getAlias();

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

}
