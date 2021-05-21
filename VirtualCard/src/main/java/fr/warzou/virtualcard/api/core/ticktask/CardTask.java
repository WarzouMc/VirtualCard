package fr.warzou.virtualcard.api.core.ticktask;

import fr.warzou.virtualcard.api.Card;

public abstract class CardTask {

    public abstract Card source();

    public abstract boolean isActive();

    public abstract int getID();

    public abstract void cancel();

}
