package fr.warzou.virtualcard.api;

import fr.warzou.virtualcard.api.core.logger.CardLogger;
import fr.warzou.virtualcard.api.core.logger.LoggerOutputStream;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.api.core.plugin.CardPluginsManager;
import fr.warzou.virtualcard.api.starter.AbstractCard;
import fr.warzou.virtualcard.api.core.ticktask.CardTick;
import fr.warzou.virtualcard.api.environment.CardEnvironment;
import fr.warzou.virtualcard.api.environment.ClockUpdater;
import fr.warzou.virtualcard.api.events.impl.SimpleEventsManager;
import fr.warzou.virtualcard.core.modules.container.container.Container;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandCaller;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandRegister;
import fr.warzou.virtualcard.utils.event.EventsManager;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;

public class Card extends AbstractCard {

    private boolean enable;

    private CardLogger logger;

    private CommandTypeListener commandTypeListener;
    private final CommandRegister commandRegister;
    private final EventsManager eventsManager;
    private ModuleManager moduleManager;
    private final CardPluginsManager pluginsManager;

    private CardEnvironment environment;
    private CardTick cardTick;
    private ClockUpdater clockUpdater;
    private CommandCaller caller;

    public Card() {
        this.enable = false;
        this.commandRegister = new CommandRegister();
        this.eventsManager = new SimpleEventsManager();

        ConsoleReader consoleReader;
        try {
            consoleReader = new ConsoleReader();
            consoleReader.setExpandEvents(false);
            this.logger = new LoggerImpl("card", consoleReader).setCard(this);
            this.caller = new CommandCaller(this);
            this.commandTypeListener = new CommandTypeListener(this, consoleReader);

            System.setOut(new PrintStream(new LoggerOutputStream(this.logger, Level.INFO), true));
            System.setErr(new PrintStream(new LoggerOutputStream(this.logger, Level.SEVERE), true));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        this.pluginsManager = new CardPluginsManager(this);
    }

    @Override
    protected void finishStartProcess() {
        System.out.println("Finish card init (modules, commands, environment, tick task)...");
        CardInitializer cardInitializer = new CardInitializer(this);
        cardInitializer.initializeCommands();
        cardInitializer.loadCommandExecutors();
        this.moduleManager = cardInitializer.initializeModules();
        if (this.moduleManager == null) {
            System.err.println("Fatal error, couldn't load modules/modulelist.json file.");
            System.exit(0);
        }
        this.environment = new CardEnvironment(this);
        this.clockUpdater = new ClockUpdater(this.environment.systemClock());
    }

    public void start() {
        if (this.enable)
            return;
        this.enable = true;
        this.cardTick = new CardTick(this);
        this.cardTick.runTaskTimer(this.clockUpdater, 0, 1);
        this.environment.finishInitialization();

        this.logger.log(Ansi.ansi().fg(Ansi.Color.CYAN).toString() + "Start card");
        this.commandTypeListener.listen();

        Container container = this.environment.container();
        if (container == null) {
            new NullPointerException("Could not get container module !").printStackTrace();
            stop();
        }
    }

    public void stop() {
        if (!this.enable)
            return;
        this.clockUpdater.cancel();
        this.logger.close();
        this.enable = false;
        this.commandTypeListener.shutdown();
    }

    public CardLogger getLogger() {
        return this.logger;
    }

    public CommandRegister getCommandRegister() {
        return this.commandRegister;
    }

    public CardEnvironment getEnvironment() {
        return this.environment;
    }

    public EventsManager getEventManager() {
        return this.eventsManager;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public CardPluginsManager getPluginsManager() {
        return this.pluginsManager;
    }

    public CommandCaller getCommandCaller() {
        return this.caller;
    }

    public CardTick getCardTick() {
        return this.cardTick;
    }

    public boolean isEnable() {
        return this.enable;
    }

    private static class LoggerImpl extends CardLogger {

        public LoggerImpl(String name, ConsoleReader consoleReader) {
            super(name, consoleReader);
        }
    }
}
