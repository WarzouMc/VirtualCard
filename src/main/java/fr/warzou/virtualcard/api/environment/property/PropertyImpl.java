package fr.warzou.virtualcard.api.environment.property;

import fr.warzou.virtualcard.api.environment.path.Property;
import org.jetbrains.annotations.NotNull;

public class PropertyImpl<T> implements Property<T> {

    private final T value;

    protected PropertyImpl(@NotNull T value) {
        this.value = value;
    }

    @Override
    public T value() {
        return this.value;
    }

}
