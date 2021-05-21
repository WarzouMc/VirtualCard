package fr.warzou.virtualcard.utils.property;

import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * {@link Iterable} of {@code T} {@link PropertyMap}.
 * <p>This class is same of {@link PropertyEntries} but contain a unique {@link Property} generic type.</p>
 * <p>{@link PropertyEntries#filter(Class)}</p>
 * @see PropertyEntries
 * @author Warzou
 * @version 0.0.2
 * @param <T> class type
 */
public interface TypedPropertyEntries<T> extends Iterable<SinglePropertyEntry<T>> {

    /**
     * That return the number of {@link Property} in filtered map
     * @return the size of {@link Iterator}
     */
    int size();

    /**
     * Check if target {@link SinglePropertyEntry} is present or not.
     * @param propertyEntry target entry
     * @return true if {@link SinglePropertyEntry} is present
     */
    boolean contain(SinglePropertyEntry<T> propertyEntry);

    /**
     * Get a {@link SinglePropertyEntry} from a key
     * @param key property key
     * @return a {@link SinglePropertyEntry} from {@code key}, return null if map doesn't contain key param.
     */
    SinglePropertyEntry<T> get(String key) throws MissingPropertyException;

    /**
     * Returns an iterator over elements of type {@code T}.
     * @return an Iterator.
     */
    @NotNull
    @Override
    Iterator<SinglePropertyEntry<T>> iterator();

}
