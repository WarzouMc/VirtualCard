package fr.warzou.virtualcard.utils.module.stream.writer;

import org.jetbrains.annotations.NotNull;

public interface Writer<T> {

    String key();

    Class<T> type();

    void write(@NotNull T value);

    boolean remove();

}
