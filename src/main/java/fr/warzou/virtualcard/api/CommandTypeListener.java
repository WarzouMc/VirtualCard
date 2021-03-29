package fr.warzou.virtualcard.api;

import fr.warzou.virtualcard.api.environment.path.PropertyMap;
import fr.warzou.virtualcard.core.modules.CardClock;
import fr.warzou.virtualcard.utils.command.AbstractCommandTypeListener;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandCaller;
import fr.warzou.virtualcard.utils.exception.property.MissingPropertyException;

import java.util.Scanner;

class CommandTypeListener extends AbstractCommandTypeListener {


    private final Card card;

    private final CommandCaller caller;
    private boolean isListen = false;

    CommandTypeListener(Card card) {
        this.card = card;

        this.caller = new CommandCaller(card);
    }

    @Override
    protected void listen() {
        if (this.isListen)
            return;
        this.isListen = true;
        Scanner scanner = new Scanner(System.in);
        String line;
        while (this.isListen && (line = scanner.nextLine()) != null) {
            PropertyMap propertyMap = this.card.getEnvironment().systemClock().getProperties();
            try {
                System.out.println(propertyMap.getProperty("now.ticks", Long.class).value());
            } catch (MissingPropertyException e) {
                e.printStackTrace();
            }
            if (!line.startsWith("/"))
                continue;
            if (!this.caller.execute(line)) {
                System.out.println("This command has fail or doesn't exist !");
                this.caller.execute("/help");
            }
        }
    }

    @Override
    protected void shutdown() {
        if (!this.isListen)
            return;
        this.isListen = false;
        System.out.println("Stop card");
    }

}
