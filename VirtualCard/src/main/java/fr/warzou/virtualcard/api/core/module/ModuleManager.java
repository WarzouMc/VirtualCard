package fr.warzou.virtualcard.api.core.module;

import fr.warzou.virtualcard.api.core.module.loader.ModuleLoader;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractPropertyMap;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.stream.ModuleStream;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ModuleManager {

    private final PropertyMap propertyMap;

    public ModuleManager(ModuleLoader loader) {
        this.propertyMap = new ImplPropertyMap();
        fillMap(loader);
    }

    public Optional<ModuleBase<?>> getModule(String name) {
        if (!this.propertyMap.containKey(name))
            return Optional.empty();
        try {
            Property<ModuleBase<?>> property = (Property<ModuleBase<?>>) this.propertyMap.getProperty(name);
            return Optional.of(property.value());
        } catch (MissingPropertyException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean addModule(ModuleBase<? extends ModuleStream> moduleBase) {
        return this.propertyMap.put(moduleBase.moduleName(), moduleBase);
    }

    public PropertyMap getModuleMap() {
        return this.propertyMap;
    }

    private void fillMap(ModuleLoader loader) {
        List<ModuleBase<?>> list = loader.getModules();
        list.forEach(moduleBase -> propertyMap.put(moduleBase.moduleName(), moduleBase));
    }

    private static class ImplPropertyMap extends AbstractPropertyMap {

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException("Could not find a module with name : " + key + ".");

            TypedPropertyEntries<T> typedPropertyEntries = this.entries.filter(clazz);
            return typedPropertyEntries.get(key).value();
        }
    }
}
