package fr.warzou.virtualcard.core.commands;

import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;

import java.util.Arrays;

public class SayCommand implements CommandExecutor {

    @Override
    public boolean execute(Command command, String[] arguments) {
        if (!command.getName().equalsIgnoreCase("say"))
            return false;

        if (arguments.length == 0)
            return false;

        StringBuilder builder = new StringBuilder();
        Arrays.asList(arguments).forEach(s -> builder.append(s).append(" "));
        builder.deleteCharAt(builder.length() - 1);
        System.out.println(builder.toString());
        return true;
    }
}
