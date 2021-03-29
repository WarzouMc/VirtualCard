package fr.warzou.virtualcard.api.environment.path;

import fr.warzou.virtualcard.utils.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public interface TypedPropertyEntry<T> extends Iterable<SinglePropertyEntry<T>> {

    int size();

    boolean contain(SinglePropertyEntry<T> propertyEntry);

    SinglePropertyEntry<T> get(String key) throws MissingPropertyException;

    @NotNull
    @Override
    Iterator<SinglePropertyEntry<T>> iterator();

}
