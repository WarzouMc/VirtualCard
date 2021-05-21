package fr.warzou.virtualcard.utils.module.stream.executor;

/**
 * Juste represent a closeable class.
 * <p>This is juste use for the interfaces {@link fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader} and {@link fr.warzou.virtualcard.utils.module.stream.executor.writer.Writer}</p>
 * @author Warzou
 * @version 0.0.2
 */
public interface Closable {

    /**
     * close
     */
    void close();

}
