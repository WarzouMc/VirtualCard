package fr.warzou.virtualcard.core.commands;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;
import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;

public class StopCommand implements CommandExecutor {

    private final Card api;

    public StopCommand(Card api) {
        this.api = api;
    }

    @Override
    public boolean execute(Command command, String[] arguments) {
        this.api.stop();
        return true;
    }

}
