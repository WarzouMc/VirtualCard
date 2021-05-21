package fr.warzou.virtualcard.core.commands;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;

public class StopCommand implements CommandExecutor {

    private final Card card;

    public StopCommand(Card card) {
        this.card = card;
    }

    @Override
    public boolean execute(Command command, String[] arguments) {
        this.card.stop();
        return true;
    }

}
