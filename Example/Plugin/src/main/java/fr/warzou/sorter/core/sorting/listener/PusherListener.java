package fr.warzou.sorter.core.sorting.listener;

import fr.warzou.virtualcard.api.core.logger.ConsoleColor;
import fr.warzou.virtualcard.api.events.events.pusher.PusherPushEvent;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.container.AbstractContainer;
import fr.warzou.virtualcard.core.modules.pusher.Pusher;
import fr.warzou.virtualcard.utils.event.utils.EventHandler;
import fr.warzou.virtualcard.utils.event.utils.EventListener;

public class PusherListener implements EventListener {

    @EventHandler
    public void onPush(PusherPushEvent event) {
        Pusher pusher = event.getPusher();
        if (!pusher.moduleName().startsWith("pusher."))
            return;

        Item<?> push = event.getPush();
        AbstractContainer from = event.getFrom();
        AbstractContainer to = event.getTo();
        if (push == null || push.getItem() == null || to == null || from == null)
            return;

        String pusherName = pusher.moduleName();
        String fromName = from.moduleName();
        String toName = to.moduleName();
        Object itemObject = push.getItem();

        System.out.println("Pusher " + ConsoleColor.FOREGROUND_MAGENTA + ConsoleColor.UNDERLINE + pusherName +
                ConsoleColor.UNDERLINE_OFF + ConsoleColor.FOREGROUND_WHITE + " has push an item :\n" +
                ConsoleColor.FOREGROUND_BLUE + "Item : " + itemObject + "\n" +
                ConsoleColor.FOREGROUND_CYAN + "From : " + fromName + "\n" +
                ConsoleColor.FOREGROUND_BLUE + "To : " + toName);
    }

}
