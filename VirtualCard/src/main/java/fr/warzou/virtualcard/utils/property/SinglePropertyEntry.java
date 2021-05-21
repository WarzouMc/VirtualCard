package fr.warzou.virtualcard.utils.property;

import org.jetbrains.annotations.NotNull;

/**
 * {@link PropertyEntries} single entry.
 * <p>This is the last step of setter and getter before {@link Property} getter.</p>
 * <p>It's not directly useful for developers out of a {@link PropertyMap} implementation.</p>
 * <p>That allows a management of properties one by one.</p>
 * @author Warzou
 * @version 0.0.2
 * @param <T> entry {@link Class} type
 */
public interface SinglePropertyEntry<T> {

    /**
     * Get this entry class type
     * @return entry class type
     */
    Class<?> type();

    /**
     * Get the key of this entry
     * @return entry key
     */
    String key();

    /**
     * Get the {@link Property} value of this entry.
     * @return entry value
     */
    Property<T> value();

    /**
     * Replace old value by a new value
     * @param value new value to set
     */
    void setValue(@NotNull T value);

}
