package fr.warzou.sorter.core.commands.start;

import fr.warzou.sorter.core.sorting.stats.SorterStats;
import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;

public class StartSortCommand implements CommandExecutor {

    private final SorterStats sorterStats;

    public StartSortCommand(SorterStats sorterStats) {
        this.sorterStats = sorterStats;
    }

    @Override
    public boolean execute(Command command, String[] arguments) {
        if (!command.getName().equals("startsort"))
            return false;
        if (this.sorterStats.isStart()) {
            System.out.println("Sorting is already start !");
            return true;
        }

        if (arguments.length == 0) {
            this.sorterStats.start(0);
            return true;
        }

        String argument = arguments[0];
        Integer integer = Integer.getInteger(argument, 1);
        this.sorterStats.start(integer);
        return true;
    }
}
