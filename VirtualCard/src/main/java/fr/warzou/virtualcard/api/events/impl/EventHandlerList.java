package fr.warzou.virtualcard.api.events.impl;

import fr.warzou.virtualcard.utils.event.utils.EventHandler;
import fr.warzou.virtualcard.utils.event.utils.EventListener;
import fr.warzou.virtualcard.exception.event.InvalidEventMethodException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

public class EventHandlerList {

    private final Collection<EventMethod> handlerList = new ArrayList<>();

    protected void registerListener(@NotNull EventListener listener) {
        if (containListener(listener))
            return;
        Method[] methods = listener.getClass().getMethods();
        for (Method method : methods) {
            if (method.isSynthetic() || method.isBridge())
                continue;

            EventHandler eventHandler = method.getAnnotation(EventHandler.class);
            if (eventHandler == null)
                continue;

            EventMethod eventMethod = null;
            try {
                eventMethod = new EventMethod(listener, method);
            } catch (InvalidEventMethodException e) {
                e.printStackTrace();
            }
            if (eventMethod == null)
                return;

            this.handlerList.add(eventMethod);
        }
    }

    protected void unregisterListener(@NotNull EventListener listener) {
        if (!containListener(listener))
            return;
        this.handlerList.removeIf(eventMethod -> eventMethod.getListener().getClass().getName()
                .equals(listener.getClass().getName()));
    }

    private boolean containListener(EventListener listener) {
        return this.handlerList.stream().anyMatch(eventMethod -> eventMethod.getListener().getClass().getName()
                .equals(listener.getClass().getName()));
    }

    protected Collection<EventMethod> handlerList() {
        return this.handlerList;
    }
}
