package fr.warzou.virtualcard.core.commands;

import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandRegister;

import java.util.List;
import java.util.Optional;

public class HelpCommand implements CommandExecutor {

    private final CommandRegister commandRegister;

    public HelpCommand(CommandRegister commandRegister) {
        this.commandRegister = commandRegister;
    }

    @Override
    public boolean execute(Command command, String[] arguments) {
        if (arguments.length == 0)
            return defaultHelpMessage();

        String targetCommandName = arguments[0];
        Optional<Command> commandOptional = this.commandRegister.safeCommandProvider(targetCommandName);
        if (!commandOptional.isPresent()) {
            System.out.println("The command '" + targetCommandName + "' doesn't exist !");
            return true;
        }
        Command targetCommand = commandOptional.get();
        StringBuilder builder = new StringBuilder();
        builder.append("Help : /").append(targetCommand.getName()).append("\n");
        System.out.println(builder.append(commandHelp(targetCommand).toString()).toString());
        return true;
    }

    private boolean defaultHelpMessage() {
        List<Command> commandList = this.commandRegister.getCommands();
        StringBuilder builder = new StringBuilder();
        builder.append("Help :\n");
        for (Command command : commandList)
            builder.append(commandHelp(command).toString());
        System.out.println(builder.toString());
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
