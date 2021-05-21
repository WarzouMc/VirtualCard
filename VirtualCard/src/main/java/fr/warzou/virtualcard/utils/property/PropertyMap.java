package fr.warzou.virtualcard.utils.property;

import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * This interface allows the links between {@link String} in key, and a generic object in value.
 * <p>
 *     This interface is quickly like a {@link java.util.Map} in the principe.
 * </p>
 * <p>
 *     However key need to be a {@link String} and the value in generic.
 * </p>
 * @author Warzou
 * @version 0.0.2
 */
public interface PropertyMap {

    /**
     * Add new value in this {@link PropertyMap}
     * @param key property key
     * @param value property value
     * @param <T> value class
     * @return true if success, and false else
     */
    <T> boolean put(@NotNull String key, T value);

    /**
     * Add new value in this {@link PropertyMap}
     * @param propertyEntry entry at add in the map.
     * @return true if success, and false else
     */
    boolean put(@NotNull SinglePropertyEntry<?> propertyEntry);

    /**
     * Modify a property in map
     * @param key target property key
     * @param value new value
     * @param <T> value class
     * @return true if success, and false else
     */
    <T> boolean set(@NotNull String key, T value);

    /**
     * This methods return the linked {@link PropertyEntries}
     * @return a {@link PropertyEntries} of this map
     */
    PropertyEntries entries();

    /**
     * Return every key in map.
     * @return a {@link Set} of all keys
     */
    Set<String> keys();

    /**
     * Return every values in map.
     * @return a {@link List} of property
     */
    List<Property<?>> values();

    /**
     * Check is this {@link PropertyMap} contain the target key.
     * @param key key to check
     * @return true if the key is present
     */
    default boolean containKey(@NotNull String key) {
        return keys().contains(key);
    }

    /**
     * That return a {@link Property} in the map compared a given key.
     * @param key target key
     * @param clazz value class
     * @param <T> {@link Property} class type
     * @return value linked to the target key
     * @throws MissingPropertyException when key is not present
     */
    <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException;

    /**
     * That return a {@link Property} in the map compared a given key.
     * @param key target key
     * @return value linked to the target key
     * @throws MissingPropertyException when key is not present
     */
    default Property<?> getProperty(@NotNull String key) throws MissingPropertyException {
        return getProperty(key, Object.class);
    }

}
