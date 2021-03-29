package fr.warzou.virtualcard.api.environment.path;

import fr.warzou.virtualcard.utils.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public interface PropertyMap {

    <T> boolean put(@NotNull String key, @NotNull T value);

    boolean put(SinglePropertyEntry<?> propertyEntry);

    <T> boolean set(String key, T value);

    PropertyEntry entries();

    Set<String> keys();

    List<Property<?>> values();

    default boolean containKey(String key) {
        return keys().contains(key);
    }

    <T> Property<T> getProperty(String key, Class<T> clazz) throws MissingPropertyException;

    default Property<?> getProperty(String key) throws MissingPropertyException {
        return getProperty(key, Object.class);
    }

}
