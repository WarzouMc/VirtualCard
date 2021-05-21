package fr.warzou.virtualcard.utils.module.stream;

import fr.warzou.virtualcard.utils.module.stream.executor.writer.Writer;
import org.jetbrains.annotations.NotNull;

/**
 * Juste allow the creation of input stream.
 * <p>Only the method {@link ModuleInputStream#write(String)} need to be implemented cause the others methods call that.</p>
 * @author Warzou
 * @version 0.0.2
 */
public interface ModuleInputStream extends ModuleStream {

    /**
     * Write a {@code T} class type value for the key property
     * @param key target key to write
     * @param type class type
     * @param <T> class Type
     * @return a {@link Writer} for the key property
     */
    <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type);

    /**
     * Write an undermined typed property.
     * <p>That same of {@code write(key, Object.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Writer} of the key property
     */
    default Writer<?> write(@NotNull String key) {
        return write(key, Object.class);
    }

    /**
     * Write an {@link Byte} property
     * <p>That same of {@code write(key, Byte.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Byte} {@link Writer} for the key property
     */
    default Writer<Byte> writeByte(@NotNull String key) {
        return write(key, Byte.class);
    }

    /**
     * Write an {@link Short} property
     * <p>That same of {@code write(key, Short.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Short} {@link Writer} for the key property
     */
    default Writer<Short> writeShort(@NotNull String key) {
        return write(key, Short.class);
    }

    /**
     * Write an {@link Integer} property
     * <p>That same of {@code write(key, Integer.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Integer} {@link Writer} for the key property
     */
    default Writer<Integer> writeInt(@NotNull String key) {
        return write(key, Integer.class);
    }

    /**
     * Write an {@link Long} property
     * <p>That same of {@code write(key, Long.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Long} {@link Writer} for the key property
     */
    default Writer<Long> writeLong(@NotNull String key) {
        return write(key, Long.class);
    }

    /**
     * Write an {@link Double} property
     * <p>That same of {@code write(key, Double.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Double} {@link Writer} for the key property
     */
    default Writer<Double> writeDouble(@NotNull String key) {
        return write(key, Double.class);
    }

    /**
     * Write an {@link Float} property
     * <p>That same of {@code write(key, Float.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Float} {@link Writer} for the key property
     */
    default Writer<Float> writeFloat(@NotNull String key) {
        return write(key, Float.class);
    }

    /**
     * Write an {@link Character} property
     * <p>That same of {@code write(key, Character.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Character} {@link Writer} for the key property
     */
    default Writer<Character> writeChar(@NotNull String key) {
        return write(key, Character.class);
    }

    /**
     * Write an {@link String} property
     * <p>That same of {@code write(key, String.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link String} {@link Writer} for the key property
     */
    default Writer<String> writeString(@NotNull String key) {
        return write(key, String.class);
    }

    /**
     * Write an {@link Boolean} property
     * <p>That same of {@code write(key, Boolean.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Boolean} {@link Writer} for the key property
     */
    default Writer<Boolean> writeBoolean(@NotNull String key) {
        return write(key, Boolean.class);
    }

    /**
     * Write an {@link Object} property
     * <p>That same of {@code write(key, Object.class);}</p>
     * @see ModuleInputStream#write(String, Class)
     * @param key target key to write
     * @return a {@link Object} {@link Writer} for the key property
     */
    default Writer<Object> writeObject(@NotNull String key) {
        return write(key, Object.class);
    }

}
