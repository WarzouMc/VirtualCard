package fr.warzou.virtualcard.utils.module.stream.executor.impl;

import fr.warzou.virtualcard.utils.module.stream.executor.writer.Writer;

/**
 * Writer implementation call when {@link fr.warzou.virtualcard.utils.module.stream.ModuleInputStream#write(String, Class)} doesn't work.
 * This {@link Writer} could do nothing and {@link #write(Object)} always return false.
 * @author Warzou
 * @version 0.0.2
 * @param <T> writer type
 */
public class NoWriter<T> implements Writer<T> {

    /**
     * Writer key
     */
    private final String key;
    /**
     * Writer class type
     */
    private final Class<T> type;

    /**
     * Create a new instance of {@link NoWriter}
     * @param key target key
     * @param type class type
     */
    public NoWriter(String key, Class<T> type) {
        this.key = key;
        this.type = type;
    }

    /**
     * If NoWriter is call, this is generally due by the key, so key could be null, empty, malformed, ...
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
     * That return always false cause this writer write anything
     * @param value value to write
     * @return always false
     */
    @Override
    public boolean write(T value) {
        return false;
    }

    /**
     * That do nothing
     */
    @Override
    public void push() {}

    /**
     * That do nothing
     */
    @Override
    public void close() {}
}
