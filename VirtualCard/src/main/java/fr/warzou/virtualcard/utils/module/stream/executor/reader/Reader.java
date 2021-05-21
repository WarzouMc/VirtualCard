package fr.warzou.virtualcard.utils.module.stream.executor.reader;

import fr.warzou.virtualcard.utils.module.stream.executor.Closable;

import java.util.Optional;

/**
 * Used by output stream to read a value.
 * <p>{@link fr.warzou.virtualcard.utils.module.stream.ModuleOutputStream#read(String, Class)} to get this interface impl.</p>
 * <p>Call {@link #read()} to read a value and to close this reader.</p>
 * <pre>
 *     ModuleOutputStream outputStream = ...;
 *     String key = "a key";
 *
 *     Reader reader = outputStream.read(key);
 *     Optional optional = reader.read();
 *     reader.close();
 *     if (!option.isPresent)
 *         return;
 *     Object value = optional.get();
 * </pre>
 * @author Warzou
 * @version 0.0.2
 * @param <T> reader type
 */
public interface Reader<T> extends Closable {

    /**
     * Target reader key.
     * @return property key
     */
    String key();

    /**
     * Returns value class type.
     * @return class type
     */
    Class<T> type();

    /**
     * Return read operation.
     * @return Empty optional if read operation has a problem, else an {@link Optional} with the property value.
     */
    Optional<T> read();

}
