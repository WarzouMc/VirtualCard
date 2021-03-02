package fr.warzou.virtualcard.utils.task;

import java.util.function.Consumer;

public interface Task<T> {

    void complete(T value);

    void whenComplete(Consumer<T> consumer);

    T get();

    boolean isDone();

}
