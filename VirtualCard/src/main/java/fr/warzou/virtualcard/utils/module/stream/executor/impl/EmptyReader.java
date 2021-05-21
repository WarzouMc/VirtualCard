package fr.warzou.virtualcard.utils.module.stream.executor.impl;

import fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader;
import fr.warzou.virtualcard.utils.module.stream.executor.writer.Writer;

import java.util.Optional;

/**
 * Writer implementation call when {@link fr.warzou.virtualcard.utils.module.stream.ModuleInputStream#write(String, Class)} doesn't work.
 * This {@link Writer} could do nothing and {@link #read()} always returns {@link Optional#empty()}.
 * @author Warzou
 * @version 0.0.2
 * @param <T> reader type
 */
public class EmptyReader<T> implements Reader<T> {

    /**
     * Target key
     */
    private final String key;
    /**
     * Value class type
     */
    private final Class<T> type;

    /**
     * Create a new instance of {@link EmptyReader}.
     * @param key target key
     * @param type value class type
     */
    public EmptyReader(String key, Class<T> type) {
        this.key = key;
        this.type = type;
    }

    /**
     * Do nothing
     */
    @Override
    public void close() {}

    /**
     * If EmptyReader is call, this is generally due by the key, so key could be null, empty, malformed, ...
     * @return target key
     */
    @Override
    public String key() {
        return this.key;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    /**
     * That always return {@link Optional#empty()}
     * @return always {@link Optional#empty()}
     */
    @Override
    public Optional<T> read() {
        return Optional.empty();
    }
}
