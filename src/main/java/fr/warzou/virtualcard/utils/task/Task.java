package fr.warzou.virtualcard.utils.task;

import java.util.function.Consumer;

/**
 * @author Redstonneur1256, Warzou
 */
public interface Task<T> {

    void complete(T value);

    void whenComplete(Consumer<T> consumer);

    T get();

    boolean isDone();

}
