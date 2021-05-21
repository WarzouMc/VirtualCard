package fr.warzou.virtualcard.api.events.events.pusher;

import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.container.AbstractContainer;
import fr.warzou.virtualcard.core.modules.pusher.Pusher;

public class PusherPushEvent extends PusherEvent{

    private final Item<?> push;
    private final AbstractContainer from;
    private final AbstractContainer to;

    public PusherPushEvent(Pusher pusher, Item<?> push, AbstractContainer from, AbstractContainer to) {
        super(pusher);
        this.push = push;
        this.from = from;
        this.to = to;
    }

    public Item<?> getPush() {
        return this.push;
    }

    public AbstractContainer getFrom() {
        return this.from;
    }

    public AbstractContainer getTo() {
        return this.to;
    }
}
