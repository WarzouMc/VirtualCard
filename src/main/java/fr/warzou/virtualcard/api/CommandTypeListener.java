package fr.warzou.virtualcard.api;

import fr.warzou.virtualcard.utils.command.AbstractCommandTypeListener;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandCaller;
import fr.warzou.virtualcard.utils.task.ThreadPool;
import fr.warzou.virtualcard.utils.task.impl.QueuedThreadPool;

import java.util.Scanner;

class CommandTypeListener extends AbstractCommandTypeListener {


    private final Card api;
    private final ThreadPool threadPool;

    private final CommandCaller caller;
    private boolean isListen = false;

    CommandTypeListener(Card api) {
        this.api = api;
        this.threadPool = new QueuedThreadPool("type_listener");

        this.caller = new CommandCaller(api);
    }

    @Override
    protected void listen() {
        if (this.isListen)
            return;
        this.isListen = true;
        this.threadPool.perform(() -> {
            Scanner scanner = new Scanner(System.in);
            String line;
            while (this.isListen && (line = scanner.nextLine()) != null) {
                if (!line.startsWith("/"))
                    continue;
                if (!this.caller.execute(line)) {
                    System.out.println("This command has fail or doesn't exist !");
                    this.caller.execute("/help");
                }
            }
        });
    }

    @Override
    protected void shutdown() {
        if (!this.isListen)
            return;
        this.isListen = false;
        System.out.println("Stop card");
        System.exit(0);
    }

}
