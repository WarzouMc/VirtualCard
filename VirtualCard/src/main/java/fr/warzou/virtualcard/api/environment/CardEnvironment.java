package fr.warzou.virtualcard.api.environment;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractPropertyMap;
import fr.warzou.virtualcard.core.modules.CardClock;
import fr.warzou.virtualcard.core.modules.captor.Captor;
import fr.warzou.virtualcard.core.modules.container.container.Container;
import fr.warzou.virtualcard.core.modules.container.conveyor.Conveyor;
import fr.warzou.virtualcard.core.modules.container.dropper.Dropper;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.file.AbstractModuleFile;
import fr.warzou.virtualcard.utils.module.stream.ModuleStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardEnvironment {

    private final Card card;
    private final PropertyMap propertyMap;

    public CardEnvironment(Card card) {
        this.card = card;
        this.propertyMap = new AbstractPropertyMap() {
            @Override
            public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
                if (!containKey(key))
                    throw new MissingPropertyException(new NotAEnvironmentComponent(), key);

                TypedPropertyEntries<T> typedPropertyEntries = this.entries.filter(clazz);
                return typedPropertyEntries.get(key).value();
            }
        };
    }

    public void finishInitialization() {
        this.propertyMap.put("system.clock", CardClock.system().init(this.card));
        this.propertyMap.put("container", Container.defaultContainer().init(this.card));
        this.propertyMap.put("dropper", Dropper.defaultDropper().init(this.card));
        this.propertyMap.put("conveyor", Conveyor.createConveyor().init(this.card));
        this.propertyMap.put("captor", Captor.systemCaptor().init(this.card));
    }

    public CardClock systemClock() {
        return CardClock.system();
    }

    @Nullable
    public Container container() {
        try {
            return this.propertyMap.getProperty("container", Container.class).value();
        } catch (MissingPropertyException e) {
            Container container = (Container) Container.defaultContainer().init(this.card);
            this.propertyMap.put("container", container);
            return container;
        }
    }

    public PropertyMap getPropertyMap() {
        return this.propertyMap;
    }

    private static class NotAEnvironmentComponent implements ModuleBase<ModuleStream> {

        private final PropertyMap propertyMap;

        private NotAEnvironmentComponent() {
            NotAEnvironmentComponent current = this;
            this.propertyMap = new AbstractPropertyMap() {
                @Override
                public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
                    if (!containKey(key))
                        throw new MissingPropertyException(current, key);

                    TypedPropertyEntries<T> typedPropertyEntries = this.entries.filter(clazz);
                    return typedPropertyEntries.get(key).value();
                }
            };
            this.propertyMap.put("name", "not_a_environment_component");
        }

        @Override
        public String moduleName() {
            try {
                return this.propertyMap.getProperty("name", String.class).value();
            } catch (MissingPropertyException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public @Nullable ModuleStream getStream() {
            return null;
        }

        @Override
        public PropertyMap getProperties() {
            return this.propertyMap;
        }

        @Override
        public @Nullable AbstractModuleFile file() {
            return null;
        }
    }
}
