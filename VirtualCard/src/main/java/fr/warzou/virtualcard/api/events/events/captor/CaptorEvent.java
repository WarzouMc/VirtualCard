package fr.warzou.virtualcard.api.events.events.captor;

import fr.warzou.virtualcard.core.modules.captor.Captor;
import fr.warzou.virtualcard.utils.event.Event;

public abstract class CaptorEvent extends Event {

    public final Captor source;

    protected CaptorEvent(Captor source) {
        this.source = source;
    }

    public Captor getSource() {
        return this.source;
    }
}
