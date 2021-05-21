package fr.warzou.virtualcard.utils.module.stream;

import fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader;
import org.jetbrains.annotations.NotNull;

import javax.naming.event.ObjectChangeListener;
import java.util.ArrayList;

/**
 * Juste allow the creation of output stream.
 * <p>Only the method {@link ModuleOutputStream#read(String)} need to be implemented cause the others methods call that.</p>
 * @author Warzou
 * @version 0.0.2
 */
public interface ModuleOutputStream extends ModuleStream {

    /**
     * Read a the value of key into a {@code T} class
     * @param key target key to read
     * @param type class type
     * @param <T> class Type
     * @return a {@link Reader} of the key property
     */
    <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type);

    /**
     * Read an undermined typed property.
     * <p>That same of {@code read(key, Object.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Reader} of the key property
     */
    default Reader<?> read(@NotNull String key) {
        return read(key, Object.class);
    }

    /**
     * Read an {@link Byte} property
     * <p>That same of {@code read(key, Byte.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Byte} {@link Reader} of the key property
     */
    default Reader<Byte> readByte(@NotNull String key) {
        return read(key, Byte.class);
    }

    /**
     * Read an {@link Short} property
     * <p>That same of {@code read(key, Short.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Short} {@link Reader} of the key property
     */
    default Reader<Short> readShort(@NotNull String key) {
        return read(key, Short.class);
    }

    /**
     * Read an {@link Integer} property
     * <p>That same of {@code read(key, Integer.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Integer} {@link Reader} of the key property
     */
    default Reader<Integer> readInt(@NotNull String key) {
        return read(key, Integer.class);
    }

    /**
     * Read an {@link Long} property
     * <p>That same of {@code read(key, Long.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Long} {@link Reader} of the key property
     */
    default Reader<Long> readLong(@NotNull String key) {
        return read(key, Long.class);
    }

    /**
     * Read an {@link Double} property
     * <p>That same of {@code read(key, Double.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Double} {@link Reader} of the key property
     */
    default Reader<Double> readDouble(@NotNull String key) {
        return read(key, Double.class);
    }

    /**
     * Read an {@link Float} property
     * <p>That same of {@code read(key, Float.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Float} {@link Reader} of the key property
     */
    default Reader<Float> readFloat(@NotNull String key) {
        return read(key, Float.class);
    }

    /**
     * Read an {@link Character} property
     * <p>That same of {@code read(key, Character.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Character} {@link Reader} of the key property
     */
    default Reader<Character> readChar(@NotNull String key) {
        return read(key, Character.class);
    }

    /**
     * Read an {@link String} property
     * <p>That same of {@code read(key, String.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link String} {@link Reader} of the key property
     */
    default Reader<String> readString(@NotNull String key) {
        return read(key, String.class);
    }

    /**
     * Read an {@link Boolean} property
     * <p>That same of {@code read(key, Boolean.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Boolean} {@link Reader} of the key property
     */
    default Reader<Boolean> readBoolean(@NotNull String key) {
        return read(key, Boolean.class);
    }

    /**
     * Read an {@link Object} property
     * <p>That same of {@code read(key, Object.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link Object} {@link Reader} of the key property
     */
    default Reader<Object> readObject(@NotNull String key) {
        return read(key, Object.class);
    }

    /**
     * Read an {@link ArrayList} property
     * <p>That same of {@code read(key, ArrayList.class);}</p>
     * @see ModuleOutputStream#read(String, Class)
     * @param key target key to read
     * @return a {@link ArrayList} {@link Reader} of the key property
     */
    default Reader<ArrayList> readArray(@NotNull String key) {
        return read(key, ArrayList.class);
    }
}
