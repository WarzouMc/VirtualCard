package fr.warzou.virtualcard.api;

import fr.warzou.virtualcard.api.core.command.CommandLoader;
import fr.warzou.virtualcard.core.commands.HelpCommand;
import fr.warzou.virtualcard.core.commands.SayCommand;
import fr.warzou.virtualcard.core.commands.StopCommand;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandRegister;

import java.io.InputStream;

class CardInitializer {

    private final Card card;

    CardInitializer(Card card) {
        this.card = card;
    }

    protected void initializeCommands() {
        String path = "command/commands.json";
        InputStream stream = Card.class.getClassLoader().getResourceAsStream(path);
        if (stream == null)
            throw new NullPointerException("Could not get commands.json resource");
        CommandLoader loader = new CommandLoader(this.card);
        loader.load(stream);
    }

    protected void loadCommandExecutors() {
        CommandRegister commandRegister = this.card.getCommandRegister();
        commandRegister.getCommand("stop").setExecutor(new StopCommand(this.card));
        commandRegister.getCommand("say").setExecutor(new SayCommand());
        commandRegister.getCommand("help").setExecutor(new HelpCommand(commandRegister));
    }

}
