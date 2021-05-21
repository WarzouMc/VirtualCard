package fr.warzou.virtualcard.utils.property;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * {@link Iterable} of {@link PropertyMap}.
 * <p>That add some methods and allows to iterate {@link Property}.</p>
 * @author Warzou
 * @version 0.0.2
 */
public interface PropertyEntries extends Iterable<SinglePropertyEntry<?>> {

    /**
     * That return the number of {@link Property} in map
     * @return the size of {@link Iterator}
     */
    int size();

    /**
     * Check if target {@link SinglePropertyEntry} is present or not.
     * @param propertyEntry target entry
     * @return true if {@link SinglePropertyEntry} is present
     */
    boolean contain(SinglePropertyEntry<?> propertyEntry);

    /**
     * Filter every {@link Property} from map with a value class type {@code type}
     * @param type class type at filter
     * @param <T> class type
     * @return new instance of {@link TypedPropertyEntries} who only contain {@link Property}
     */
    <T> TypedPropertyEntries<T> filter(Class<T> type);

    /**
     * Get a {@link SinglePropertyEntry} from a key
     * @param key property key
     * @return a {@link SinglePropertyEntry} from {@code key}, return null if map doesn't contain key param.
     */
    SinglePropertyEntry<?> get(String key);

    /**
     * Add a entry in map.
     * @param propertyEntry {@link SinglePropertyEntry} to add
     * @return true if success, and false else
     */
    boolean add(SinglePropertyEntry<?> propertyEntry);

    /**
     * Remove a entry in map.
     * @param propertyEntry {@link SinglePropertyEntry} to remove
     * @return true if success, and false else
     */
    boolean remove(SinglePropertyEntry<?> propertyEntry);

    /**
     * Remove a entry in map from its index.
     * @param index {@link SinglePropertyEntry} index to remove
     * @return true if success, and false else
     */
    boolean remove(int index);

    /**
     * Returns an iterator over elements of type {@code T}.
     * @return an Iterator.
     */
    @NotNull
    @Override
    Iterator<SinglePropertyEntry<?>> iterator();

}
