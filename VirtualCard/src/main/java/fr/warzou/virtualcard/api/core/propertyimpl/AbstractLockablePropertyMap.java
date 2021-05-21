package fr.warzou.virtualcard.api.core.propertyimpl;

import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.SinglePropertyEntry;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLockablePropertyMap extends AbstractPropertyMap {

    private boolean lock;

    public AbstractLockablePropertyMap() {
        super(new LockablePropertyEntries());
        this.lock = false;
    }

    @Override
    public <T> boolean put(@NotNull String key, T value) {
        if (this.lock) {
            lockError();
            return false;
        }
        return super.put(key, value);
    }

    @Override
    public boolean put(@NotNull SinglePropertyEntry<?> propertyEntry) {
        if (this.lock) {
            lockError();
            return false;
        }
        propertyEntry = LockableSinglePropertyEntry.asLockable(propertyEntry);
        return super.put(propertyEntry);
    }

    @Override
    public <T> boolean set(@NotNull String key, T value) {
        if (this.lock) {
            lockError();
            return false;
        }
        return super.set(key, value);
    }

    public void lock() {
        this.lock = true;
        ((LockablePropertyEntries) this.entries).lock();
    }

    public boolean isLock() {
        return this.lock;
    }

    protected static void lockError() {
        try {
            throw new UnsupportedOperationException("This property map is lock.");
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "PropertyMap{" +
                "lock=" + lock +
                ", entries=" + entries +
                '}';
    }

    protected static final class LockableSinglePropertyEntry<T> implements SinglePropertyEntry<T> {

        private final String key;
        private T value;
        private Property<T> property;

        private LockableSinglePropertyEntry(String key, T value) {
            this.key = key;
            this.value = value;
            this.property = new PropertyImpl<>(key, this.value);
        }

        protected static <U> LockableSinglePropertyEntry<U> asLockable(SinglePropertyEntry<U> propertyEntry) {
            return new LockableSinglePropertyEntry<>(propertyEntry.key(), propertyEntry.value().value());
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
            this.property = new PropertyImpl<>(key, this.value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;

            if (!o.getClass().isAssignableFrom(getClass()))
                return false;

            AbstractLockablePropertyMap.LockableSinglePropertyEntry<?> singlePropertyEntry =
                    (AbstractLockablePropertyMap.LockableSinglePropertyEntry<?>) o;
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
