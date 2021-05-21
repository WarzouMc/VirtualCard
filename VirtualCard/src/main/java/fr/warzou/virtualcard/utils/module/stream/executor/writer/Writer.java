package fr.warzou.virtualcard.utils.module.stream.executor.writer;

import fr.warzou.virtualcard.utils.module.stream.executor.Closable;

/**
 * Used by input stream to write a value.
 * <p>{@link fr.warzou.virtualcard.utils.module.stream.ModuleInputStream#write(String, Class)} to get this interface impl.</p>
 * <p>Call {@link #write(Object)} to write a value, then {@link #push()} to save it and {@link #close()} to close this writer.</p>
 * <pre>
 *     ModuleInputStream inputStream = ...;
 *     String key = "a key";
 *     Object value = new Object();
 *
 *     Writer writer = inputStream.write(key);
 *     writer.write(value);
 *     writer.push();
 *     writer.close();
 * </pre>
 * @author Warzou
 * @version 0.0.2
 * @param <T> writer type
 */
public interface Writer<T> extends Closable {

    /**
     * Target writer key.
     * @return property key
     */
    String key();

    /**
     * Returns value class type.
     * @return class type
     */
    Class<T> type();

    /**
     * Write a value.
     * @param value value to write
     * @return true if success, false else
     */
    boolean write(T value);

    /**
     * Save change.
     */
    void push();

}
