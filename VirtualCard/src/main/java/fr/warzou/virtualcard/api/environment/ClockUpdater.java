package fr.warzou.virtualcard.api.environment;

import fr.warzou.virtualcard.api.core.ticktask.CardRunnable;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.core.modules.CardClock;

public class ClockUpdater extends CardRunnable {

    private final CardClock cardClock;

    public ClockUpdater(CardClock cardClock) {
        this.cardClock = cardClock;
    }

    @Override
    protected void run() {
        PropertyMap propertyMap = this.cardClock.getProperties();
        propertyMap.set("now.ticks", this.cardClock.nowTicks());
        propertyMap.set("now.seconds", this.cardClock.nowSeconds());
        propertyMap.set("now.minutes", this.cardClock.nowMinutes());
        propertyMap.set("now.hours", this.cardClock.nowHours());
    }
}
