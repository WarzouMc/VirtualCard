package fr.warzou.virtualcard.api.core.plugin;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractLockablePropertyMap;
import fr.warzou.virtualcard.exception.plugin.AlreadyRegisteredPluginException;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

public class PluginRegister {

    private final Card owner;
    private final PropertyMap propertyMap;

    PluginRegister(Card owner) {
        this.owner = owner;
        this.propertyMap = new LockablePropertyMap();
    }

    public boolean register(CardPlugin plugin) {
        String name = plugin.getInformation().name();
        if (this.propertyMap.containKey(name)) {
            try {
                throw new AlreadyRegisteredPluginException(name);
            } catch (AlreadyRegisteredPluginException e) {
                e.printStackTrace();
                return false;
            }
        }
        return this.propertyMap.put(name, plugin);
    }

    public PropertyMap getPropertyMap() {
        return this.propertyMap;
    }

    private static class LockablePropertyMap extends AbstractLockablePropertyMap {

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(key);

            TypedPropertyEntries<T> typedPropertyEntries = this.entries.filter(clazz);
            return typedPropertyEntries.get(key).value();
        }
    }

}
