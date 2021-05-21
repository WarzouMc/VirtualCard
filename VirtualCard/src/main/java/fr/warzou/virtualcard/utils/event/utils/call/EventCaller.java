package fr.warzou.virtualcard.utils.event.utils.call;

import fr.warzou.virtualcard.utils.event.Event;

/**
 * Allow to call {@link Event}.
 * @author Warzou
 * @version 0.0.1
 */
public interface EventCaller {

    /**
     * Call a {@link Event}
     * @param event target {@link Event}
     */
    void call(Event event);

}
