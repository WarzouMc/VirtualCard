package fr.warzou.virtualcard.api.environment;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.environment.path.Property;
import fr.warzou.virtualcard.api.environment.path.PropertyMap;
import fr.warzou.virtualcard.api.environment.path.TypedPropertyEntry;
import fr.warzou.virtualcard.api.environment.property.AbstractPropertyMap;
import fr.warzou.virtualcard.core.modules.CardClock;
import fr.warzou.virtualcard.utils.exception.property.MissingPropertyException;

public class CardEnvironment {

    private final Card card;

    private final PropertyMap propertyMap;

    public CardEnvironment(Card card) {
        this.card = card;
        this.propertyMap = new AbstractPropertyMap() {
            @Override
            public <T> Property<T> getProperty(String key, Class<T> clazz) throws MissingPropertyException {
                if (!containKey(key))
                    throw new MissingPropertyException(new NotAEnvironmentComponent(), key);

                TypedPropertyEntry<T> typedPropertyEntry = this.entries.filter(clazz);
                return typedPropertyEntry.get(key).value();
            }
        };
        this.propertyMap.put("system.clock", systemClock());
    }

    public CardClock systemClock() {
        return CardClock.system();
    }

    public PropertyMap getPropertyMap() {
        return this.propertyMap;
    }

    private static class NotAEnvironmentComponent implements EnvironmentComponent {

        private final PropertyMap propertyMap;

        private NotAEnvironmentComponent() {
            NotAEnvironmentComponent current = this;
            this.propertyMap = new AbstractPropertyMap() {
                @Override
                public <T> Property<T> getProperty(String key, Class<T> clazz) throws MissingPropertyException {
                    if (!containKey(key))
                        throw new MissingPropertyException(current, key);

                    TypedPropertyEntry<T> typedPropertyEntry = this.entries.filter(clazz);
                    return typedPropertyEntry.get(key).value();
                }
            };
            this.propertyMap.put("name", "not_a_environment_component");
        }

        @Override
        public String name() {
            try {
                return this.propertyMap.getProperty("name", String.class).value();
            } catch (MissingPropertyException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public PropertyMap getProperties() {
            return this.propertyMap;
        }
    }

}
