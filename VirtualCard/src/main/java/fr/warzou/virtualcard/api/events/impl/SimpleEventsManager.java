package fr.warzou.virtualcard.api.events.impl;

import fr.warzou.virtualcard.utils.event.Event;
import fr.warzou.virtualcard.utils.event.EventsManager;
import fr.warzou.virtualcard.utils.event.utils.EventListener;
import fr.warzou.virtualcard.utils.event.utils.call.EventCaller;
import org.jetbrains.annotations.NotNull;

public class SimpleEventsManager implements EventsManager {

    private final EventHandlerList eventHandlerList;
    private final EventCaller eventCaller;

    public SimpleEventsManager() {
        this.eventHandlerList = new EventHandlerList();
        this.eventCaller = new SimpleEventCaller(this.eventHandlerList);
    }

    @Override
    public void registerListener(@NotNull EventListener listener) {
        this.eventHandlerList.registerListener(listener);
    }

    @Override
    public void unregisterListener(@NotNull EventListener listener) {
        this.eventHandlerList.unregisterListener(listener);
    }

    @Override
    public void callEvent(@NotNull Event event) {
        this.eventCaller.call(event);
    }
}
