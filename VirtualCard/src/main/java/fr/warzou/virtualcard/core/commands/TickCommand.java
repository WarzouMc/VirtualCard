package fr.warzou.virtualcard.core.commands;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.logger.ConsoleColor;
import fr.warzou.virtualcard.api.core.ticktask.CardTick;
import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;

public class TickCommand implements CommandExecutor {

    private final CardTick.TickLog tickLog;

    public TickCommand(CardTick.TickLog tickLog) {
        this.tickLog = tickLog;
    }

    @Override
    public boolean execute(Command command, String[] arguments) {
        if (!command.getName().equalsIgnoreCase("tick"))
            return false;

        double fiveMinutes = round(this.tickLog.getThirdRankTickLog(), 3);
        double oneMinute = round(this.tickLog.getSecondRankTickLog(), 3);
        double tenSeconds = round(this.tickLog.getFirstRankTickLog(), 3);
        double overall = round(this.tickLog.getOverallTickLog(), 3);

        System.out.println("Tick log\n" +
                "5min : " + colorTick(fiveMinutes) + " ticks/s, " +
                "1min : " + colorTick(oneMinute) + " ticks/s, " +
                "10s : " + colorTick(tenSeconds) + " ticks/s\n" +
                "Last second : " + colorTick(this.tickLog.lastSecond()) + " ticks/s\n" +
                "Overall : " + colorTick(overall) + " ticks/s");
        return true;
    }

    private String colorTick(double ticks) {
        return (ticks > 19 ? (ticks > 19.5 ? ConsoleColor.FOREGROUND_GREEN : ConsoleColor.FOREGROUND_YELLOW)
                : ConsoleColor.FOREGROUND_RED) + ticks + ConsoleColor.RESET;
    }

    private double round(double base, int round) {
        return Math.round(base * Math.pow(10, round)) / Math.pow(10, round);
    }
}
