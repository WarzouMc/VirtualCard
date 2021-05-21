package fr.warzou.virtualcard.api.events.events.pusher;

import fr.warzou.virtualcard.core.modules.pusher.Pusher;
import fr.warzou.virtualcard.utils.event.Event;

public abstract class PusherEvent extends Event {

    private final Pusher pusher;

    protected PusherEvent(Pusher pusher) {
        this.pusher = pusher;
    }

    public Pusher getPusher() {
        return this.pusher;
    }
}
