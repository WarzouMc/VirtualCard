package fr.warzou.virtualcard.api;

import fr.warzou.virtualcard.api.core.command.CommandLoader;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.api.core.module.loader.ModuleLoader;
import fr.warzou.virtualcard.core.commands.*;
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
        if (stream == null) {
            try {
                throw new NullPointerException("Could not get command/commands.json resource");
            } catch (NullPointerException e) {
                e.printStackTrace();
                return;
            }
        }
        CommandLoader loader = new CommandLoader(this.card);
        loader.load(stream);
    }

    protected ModuleManager initializeModules() {
        String listPath = "modules/modulelist.json";
        InputStream inputStream = Card.class.getClassLoader().getResourceAsStream(listPath);
        if (inputStream == null) {
            try {
                throw new NullPointerException("Could not get modules/modulelist.json resource");
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }
        ModuleLoader moduleLoader = new ModuleLoader();
        return moduleLoader.load(this.card, inputStream);
    }

    protected void loadCommandExecutors() {
        CommandRegister commandRegister = this.card.getCommandRegister();
        commandRegister.getCommand("stop").setExecutor(new StopCommand(this.card));
        commandRegister.getCommand("say").setExecutor(new SayCommand(this.card));
        commandRegister.getCommand("help").setExecutor(new HelpCommand(this.card, commandRegister));
        commandRegister.getCommand("packet").setExecutor(new PacketCommand(this.card));
        commandRegister.getCommand("plugins").setExecutor(new PluginsCommand(this.card));
        commandRegister.getCommand("pluginhelp").setExecutor(new PluginHelpCommand(this.card));
    }

}
