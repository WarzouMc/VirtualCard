package fr.warzou.virtualcard.api.environment.path;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface PropertyEntry extends Iterable<SinglePropertyEntry<?>> {

    int size();

    boolean contain(SinglePropertyEntry<?> propertyEntry);

    <T> TypedPropertyEntry<T> filter(Class<T> type);

    SinglePropertyEntry<?> get(String key);

    boolean add(SinglePropertyEntry<?> propertyEntry);

    boolean remove(SinglePropertyEntry<?> propertyEntry);

    boolean remove(int index);

    @NotNull
    @Override
    Iterator<SinglePropertyEntry<?>> iterator();

}
