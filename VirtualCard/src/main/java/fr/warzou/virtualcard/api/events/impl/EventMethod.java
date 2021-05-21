package fr.warzou.virtualcard.api.events.impl;

import fr.warzou.virtualcard.utils.event.Event;
import fr.warzou.virtualcard.utils.event.utils.EventHandler;
import fr.warzou.virtualcard.utils.event.utils.EventListener;
import fr.warzou.virtualcard.exception.event.InvalidEventMethodException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventMethod {

    private final Method method;

    private Class<? extends Event> targetEvent;
    private final EventListener listener;
    private EventHandler.EventPriority priority;

    protected EventMethod(@NotNull EventListener listener, @NotNull Method method) throws InvalidEventMethodException {
        this.listener = listener;
        this.method = method;
        buildParameters();
    }

    private void buildParameters() throws InvalidEventMethodException {
        if (this.method.getParameterCount() != 1)
            throw new InvalidEventMethodException("Methods with the \"@EventHandler\" annotation must have only one argument.\n" +
                    "Methode name : " + this.method.getName() + "\n" +
                    "Class name : " + this.listener.getClass().getName());

        Class<?> parameters = this.method.getParameterTypes()[0];
        if (!Event.class.isAssignableFrom(parameters))
            throw new InvalidEventMethodException(parameters.getName() + " is not a Event.\n" +
                    "Methode name : " + this.method.getName() + "\n" +
                    "Class name : " + this.listener.getClass().getName());

        this.targetEvent = parameters.asSubclass(Event.class);
        EventHandler eventHandler = this.method.getAnnotation(EventHandler.class);
        this.priority = eventHandler.eventPriority();
    }

    protected Method getMethod() {
        return this.method;
    }

    protected final Class<? extends Event> getTargetEvent() {
        return this.targetEvent;
    }

    protected EventListener getListener() {
        return this.listener;
    }

    protected EventHandler.EventPriority getPriority() {
        return this.priority;
    }

    protected void invoke(Event event) {
        if (!event.getClass().isAssignableFrom(this.targetEvent))
            return;
        this.method.setAccessible(true);
        try {
            this.method.invoke(this.listener, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
