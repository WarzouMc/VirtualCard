package fr.warzou.virtualcard.api;

import fr.warzou.virtualcard.api.core.ticktask.CardTask;
import fr.warzou.virtualcard.utils.command.AbstractCommandTypeListener;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandCaller;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.logging.Level;

class CommandTypeListener extends AbstractCommandTypeListener {

    private final Card card;
    private final ConsoleReader reader;

    private final CommandCaller caller;
    private CardTask cardTask;
    private boolean isListen = false;

    CommandTypeListener(Card card, ConsoleReader reader) {
        this.card = card;

        this.caller = this.card.getCommandCaller();
        this.reader = reader;
    }

    @Override
    protected void listen() {
        if (this.isListen)
            return;
        this.isListen = true;

        String prompt = ">> ";
        new Thread(() -> {
            while (this.isListen) {
                String line = null;
                try {
                    line = reader.readLine(prompt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!caller.execute(line)) {
                    card.getLogger().log("This command has fail or doesn't exist !", Level.WARNING);
                    caller.execute("help");
                }
            }
        }).start();
    }

    @Override
    protected void shutdown() {
        if (!this.isListen)
            return;
        this.isListen = false;
        this.card.getLogger().log("Stop card");
    }
}
