package fr.warzou.virtualcard.utils.event;

import fr.warzou.virtualcard.utils.event.utils.EventListener;

/**
 * Basic events manager methods.
 * @author Warzou
 * @version 0.0.1
 */
public interface EventsManager {

    /**
     * Add a new listener to listen events.
     * @param listener target {@link EventListener}
     */
    void registerListener(EventListener listener);

    /**
     * Remove a registered {@link EventListener}.
     * @param listener target {@link EventListener}
     */
    void unregisterListener(EventListener listener);

    /**
     * Call an {@link Event}
     * @param event target event
     */
    void callEvent(Event event);

}
