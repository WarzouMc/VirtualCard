package fr.warzou.virtualcard.utils.event.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allow to recognize methods to trigger when an event is call.
 * @author Warzou
 * @version 0.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    /**
     * Returns method priority
     * @return method call priority
     */
    EventPriority eventPriority() default EventPriority.LOW;

    /**
     * {@link EventHandler} priorities enumeration
     * @author Warzou
     * @version 0.0.1
     */
    enum EventPriority {
        /**
         * Max priority
         */
        HIGH,
        /**
         * Call after {@link EventPriority#HIGH} priority methods but before {@link EventPriority#LOW} priority methods.
         */
        MEDIUM,
        /**
         * Min priority
         */
        LOW
    }

}
