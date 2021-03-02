package fr.warzou.virtualcard.api;

import fr.warzou.virtualcard.utils.command.commandsystem.CommandRegister;

public class Card {

    private boolean enabled = false;

    private final CommandTypeListener commandTypeListener;
    private final CommandRegister commandRegister;

    public Card() {
        this.commandRegister = new CommandRegister();
        this.commandTypeListener = new CommandTypeListener(this);

        CardInitializer cardInitializer = new CardInitializer(this);
        cardInitializer.initializeCommands();
        cardInitializer.loadCommandExecutors();
    }

    public void start() {
        if (this.enabled)
            return;
        this.enabled = true;
        this.commandTypeListener.listen();
        System.out.println("Start card");
    }

    public void stop() {
        if (!this.enabled)
            return;
        this.enabled = false;
        this.commandTypeListener.shutdown();
    }

    public CommandRegister getCommandRegister() {
        return this.commandRegister;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
