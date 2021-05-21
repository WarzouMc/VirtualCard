package fr.warzou.virtualcard.api.core.propertyimpl;

import fr.warzou.virtualcard.utils.property.Property;
import org.jetbrains.annotations.NotNull;

public class PropertyImpl<T> implements Property<T> {

    private final String key;
    private final T value;

    protected PropertyImpl(@NotNull String key, @NotNull T value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public T value() {
        return this.value;
    }

}
