package fr.warzou.virtualcard.api.events.impl;

import fr.warzou.virtualcard.utils.event.Event;
import fr.warzou.virtualcard.utils.event.utils.call.EventCaller;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleEventCaller implements EventCaller {

    private final EventHandlerList eventHandlerList;

    public SimpleEventCaller(EventHandlerList handlerList) {
        this.eventHandlerList = handlerList;
    }

    @Override
    public void call(Event event) {
        Collection<EventMethod> handlerList = this.eventHandlerList.handlerList();
        List<EventMethod> targetMethods = handlerList.stream()
                .filter(eventMethod -> event.getClass().isAssignableFrom(eventMethod.getTargetEvent()))
                .sorted((o1, o2) -> Integer.compare(o2.getPriority().ordinal(), o1.getPriority().ordinal()))
                .collect(Collectors.toList());
        Collections.reverse(targetMethods);
        targetMethods.forEach(method -> method.invoke(event));
    }
}
