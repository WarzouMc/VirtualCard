package fr.warzou.virtualcard.exception.command;

public class CommandFormatException extends Exception {

    public CommandFormatException(String plugin, String name, String reason) {
        super("Could not load command '" + name + "' from plugin '" + plugin + "' caused by : " +
                reason);
    }

}
