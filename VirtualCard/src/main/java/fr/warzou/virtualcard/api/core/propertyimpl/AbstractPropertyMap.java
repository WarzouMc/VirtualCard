package fr.warzou.virtualcard.api.core.propertyimpl;

import fr.warzou.virtualcard.utils.property.*;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractPropertyMap implements PropertyMap {

    protected final PropertyEntries entries;

    public AbstractPropertyMap() {
        this(new PropertyEntriesImpl());
    }

    protected AbstractPropertyMap(PropertyEntries entries) {
        this.entries = entries;
    }

    @Override
    public <T> boolean put(@NotNull String key, T value) {
        if (containKey(key))
            return set(key, value);
        SinglePropertyEntry<T> propertyEntry = new SinglePropertyEntryImpl<>(key, value);
        return put(propertyEntry);
    }

    @Override
    public boolean put(@NotNull SinglePropertyEntry<?> propertyEntry) {
        return this.entries.add(propertyEntry);
    }

    @Override
    public <T> boolean set(@NotNull String key, T value) {
        if (!containKey(key))
            return put(key, value);
        try {
            SinglePropertyEntry<T> singlePropertyEntry = this.entries.filter((Class<T>) value.getClass()).get(key);
            singlePropertyEntry.setValue(value);
            return true;
        } catch (MissingPropertyException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public PropertyEntries entries() {
        return this.entries;
    }

    @Override
    public Set<String> keys() {
        Set<String> set = new HashSet<>();
        for (SinglePropertyEntry<?> entry : this.entries)
            set.add(entry.key());
        return set;
    }

    @Override
    public List<Property<?>> values() {
        List<Property<?>> properties = new ArrayList<>();
        for (SinglePropertyEntry<?> entry : this.entries)
            properties.add(entry.value());
        return properties;
    }

    @Override
    public abstract <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException;

    @Override
    public String toString() {
        return "PropertyMap{" +
                "entries=" + entries +
                '}';
    }

    private static final class SinglePropertyEntryImpl<T> implements SinglePropertyEntry<T> {

        private final String key;
        private T value;
        private Property<T> property;

        private SinglePropertyEntryImpl(String key, T value) {
            this.key = key;
            this.value = value;
            this.property = new PropertyImpl<>(key, this.value);
        }

        @Override
        public Class<?> type() {
            return this.value.getClass();
        }

        @Override
        public String key() {
            return this.key;
        }

        @Override
        public Property<T> value() {
            return this.property;
        }

        @Override
        public void setValue(@NotNull T value) {
            this.value = value;
            this.property = new PropertyImpl<>(key, value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;

            if (!o.getClass().isAssignableFrom(getClass()))
                return false;

            SinglePropertyEntryImpl<?> singlePropertyEntry = (SinglePropertyEntryImpl<?>) o;
            if (!singlePropertyEntry.key.equals(this.key))
                return false;
            return singlePropertyEntry.value.equals(this.value);
        }

        @Override
        public String toString() {
            return "SinglePropertyEntry{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    ", property=" + property +
                    '}';
        }
    }
}
