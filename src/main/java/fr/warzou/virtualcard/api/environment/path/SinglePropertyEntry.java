package fr.warzou.virtualcard.api.environment.path;

import org.jetbrains.annotations.NotNull;

public interface SinglePropertyEntry<T> {

    Class<?> type();

    String key();

    Property<T> value();

    void setValue(@NotNull T value);

}
