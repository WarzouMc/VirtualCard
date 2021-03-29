package fr.warzou.virtualcard.api;

import fr.warzou.virtualcard.api.environment.CardEnvironment;
import fr.warzou.virtualcard.api.environment.ticktask.CardRunnable;
import fr.warzou.virtualcard.api.environment.ticktask.CardTick;
import fr.warzou.virtualcard.api.environment.ticktask.ClockUpdater;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandRegister;

public class Card {

    private boolean enable;

    private final CommandTypeListener commandTypeListener;
    private final CommandRegister commandRegister;

    private final CardEnvironment environment;
    private CardTick cardTick;

    public Card() {
        this.enable = false;
        this.commandRegister = new CommandRegister();
        this.commandTypeListener = new CommandTypeListener(this);

        CardInitializer cardInitializer = new CardInitializer(this);
        cardInitializer.initializeCommands();
        cardInitializer.loadCommandExecutors();

        this.environment = new CardEnvironment(this);
    }

    public void start() {
        if (this.enable)
            return;
        this.enable = true;
        this.cardTick = new CardTick(this);
        System.out.println("Start card");
        CardRunnable runnable = this.cardTick.runTaskTimer(new ClockUpdater(this.environment.systemClock()));
        this.commandTypeListener.listen();
    }

    public void stop() {
        if (!this.enable)
            return;
        this.enable = false;
        this.commandTypeListener.shutdown();
    }

    public CommandRegister getCommandRegister() {
        return this.commandRegister;
    }

    public CardEnvironment getEnvironment() {
        return this.environment;
    }

    public boolean isEnable() {
        return this.enable;
    }
}
