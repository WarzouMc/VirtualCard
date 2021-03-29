package fr.warzou.virtualcard.core.modules;

import fr.warzou.virtualcard.api.environment.property.AbstractPropertyMap;
import fr.warzou.virtualcard.api.environment.EnvironmentComponent;
import fr.warzou.virtualcard.api.environment.path.Property;
import fr.warzou.virtualcard.api.environment.path.PropertyMap;
import fr.warzou.virtualcard.api.environment.path.TypedPropertyEntry;
import fr.warzou.virtualcard.api.environment.ticktask.CardTick;
import fr.warzou.virtualcard.utils.exception.property.MissingPropertyException;

import java.time.Instant;

public abstract class CardClock implements EnvironmentComponent {

    public static CardClock system() {
        return new CardSystemClock();
    }

    private final PropertyMap propertyMap;

    public CardClock() {
        this.propertyMap = new ClockAbstractPropertyMap(this);
        this.propertyMap.put("now.ticks", nowTicks());
        this.propertyMap.put("now.seconds", nowSeconds());
        this.propertyMap.put("now.minutes", nowMinutes());
        this.propertyMap.put("now.hours", nowHours());
        this.propertyMap.put("instant", instant());
        this.propertyMap.put("name", "system.clock");
    }

    public abstract Instant instant();

    public abstract long nowTicks();

    public abstract long nowSeconds();

    public abstract long nowMinutes();

    public abstract long nowHours();

    @Override
    public String name() {
        try {
            return (String) this.propertyMap.getProperty("name").value();
        } catch (MissingPropertyException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PropertyMap getProperties() {
        return this.propertyMap;
    }

    private static class CardSystemClock extends CardClock {

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(nowTicks() * Math.round(1000 * CardTick.TICK));
        }

        @Override
        public long nowTicks() {
            return Math.round(System.currentTimeMillis() / (CardTick.TICK * 1000));
        }

        @Override
        public long nowSeconds() {
            return Math.round(nowTicks() * CardTick.TICK);
        }

        @Override
        public long nowMinutes() {
            long seconds = nowSeconds();
            return seconds / 60;
        }

        @Override
        public long nowHours() {;
            long minutes = nowMinutes();
            return minutes / 60;
        }
    }

    private static class ClockAbstractPropertyMap extends AbstractPropertyMap {

        private final CardClock clock;

        private ClockAbstractPropertyMap(CardClock clock) {
            super();
            this.clock = clock;
        }

        @Override
        public <T> Property<T> getProperty(String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(this.clock, key);

            TypedPropertyEntry<T> typedPropertyEntry = this.entries.filter(clazz);
            return typedPropertyEntry.get(key).value();
        }
    }

}
