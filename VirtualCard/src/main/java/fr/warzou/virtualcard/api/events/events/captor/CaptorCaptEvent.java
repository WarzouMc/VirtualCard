package fr.warzou.virtualcard.api.events.events.captor;

import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.captor.Captor;

public class CaptorCaptEvent extends CaptorEvent {

    private final long when;
    private final Item<?> capt;
    private final Captor.CaptResult result;

    public CaptorCaptEvent(Captor source, long when, Item<?> capt, Captor.CaptResult result) {
        super(source);
        this.when = when;
        this.capt = capt;
        this.result = result;
    }

    public long getWhen() {
        return this.when;
    }

    public Item<?> getCapt() {
        return this.capt;
    }

    public Captor.CaptResult getResult() {
        return this.result;
    }

    public boolean isNull() {
        return this.capt == null || this.capt.getItem() == null;
    }
}
