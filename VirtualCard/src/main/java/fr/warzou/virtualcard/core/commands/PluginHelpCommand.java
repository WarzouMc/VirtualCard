package fr.warzou.virtualcard.core.commands;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.plugin.CardPluginsManager;
import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandRegister;
import fr.warzou.virtualcard.utils.plugin.PluginInformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PluginHelpCommand implements CommandExecutor {

    private final Card card;
    private final CommandRegister commandRegister;

    public PluginHelpCommand(Card card) {
        this.card = card;
        this.commandRegister = this.card.getCommandRegister();
    }

    @Override
    public boolean execute(Command command, String[] arguments) {
        if (arguments.length == 0)
            return this.card.getCommandCaller().execute("help pluginhelp");

        CardPluginsManager pluginsManager = this.card.getPluginsManager();
        PluginInformation information = pluginsManager.getPluginInformation(arguments[0]);
        if (information == null) {
            System.err.println(arguments[0] + " is not in plugin list !");
            return true;
        }
        return defaultHelpMessage(arguments[0]);
    }

    private boolean defaultHelpMessage(String pluginName) {
        List<Command> commandList = new ArrayList<>(this.commandRegister.getCommands());
        commandList.sort(Comparator.comparing(Command::getName));

        List<Command> pluginCommands = commandList.stream().filter(command -> command.source().equalsIgnoreCase(pluginName))
                .collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        builder.append("Help from plugin '").append(pluginName).append("' :\n");
        for (Command command : pluginCommands)
            builder.append(commandHelp(command));
        this.card.getLogger().log(builder.toString(), Level.WARNING);
        return true;
    }

    private StringBuilder commandHelp(Command command) {
        StringBuilder builder = new StringBuilder();
        builder.append("/").append(command.getName()).append(" : ");
        builder.append(command.getDescription()).append("\n");
        if (command.getHelpMessage() != null)
            builder.append("\thelp : ").append(command.getHelpMessage()).append("\n");
        if (command.getAlias().length == 0)
            return builder;
        builder.append("\talias :\n");
        for (String alias : command.getAlias())
            builder.append("\t\t- ").append("/").append(alias.toLowerCase()).append("\n");
        return builder;
    }
}
