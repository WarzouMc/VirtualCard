package fr.warzou.virtualcard.core.commands;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;

import java.util.Set;

public class PluginsCommand implements CommandExecutor {

    private final Card card;

    public PluginsCommand(Card card) {
        this.card = card;
    }

    @Override
    public boolean execute(Command command, String[] arguments) {
        if (!command.getName().equalsIgnoreCase("plugins"))
            return false;

        Set<String> plugins = this.card.getPluginsManager().plugins();
        int count = plugins.size();
        String string = plugins.toString();
        System.out.println("You have " + count + " registered plugin" + (count > 1 ? "s" : "") +
                (count == 0 ? "" : "\n" + string));
        return true;
    }
}
