package fr.warzou.virtualcard.api.core.propertyimpl;

import fr.warzou.virtualcard.utils.property.PropertyEntries;
import fr.warzou.virtualcard.utils.property.SinglePropertyEntry;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class LockablePropertyEntries implements PropertyEntries {

    private AbstractLockablePropertyMap.LockableSinglePropertyEntry<?>[] array =
            new AbstractLockablePropertyMap.LockableSinglePropertyEntry<?>[0];

    private boolean lock;

    public LockablePropertyEntries() {
        this.lock = false;
    }

    @Override
    public int size() {
        return this.array.length;
    }

    @Override
    public boolean contain(SinglePropertyEntry<?> propertyEntry) {
        return Arrays.asList(this.array).contains(AbstractLockablePropertyMap.LockableSinglePropertyEntry
                .asLockable(propertyEntry));
    }

    @Override
    public <T> TypedPropertyEntries<T> filter(Class<T> type) {
        LockableTypedPropertyEntries<T> typedEntry = new LockableTypedPropertyEntries<>(type);
        for (SinglePropertyEntry<?> propertyEntry : this) {
            if (type.isAssignableFrom(propertyEntry.type()))
                typedEntry.add((SinglePropertyEntry) propertyEntry);
        }
        typedEntry.lock();
        return typedEntry;
    }

    @Override
    public SinglePropertyEntry<?> get(String key) {
        if (!containKey(key))
            try {
                throw new MissingPropertyException(key);
            } catch (MissingPropertyException e) {
                e.printStackTrace();
                return null;
            }
        return Arrays.stream(this.array).filter(propertyEntry -> propertyEntry.key().equals(key)).findFirst().orElse(null);
    }

    @Override
    public boolean add(SinglePropertyEntry<?> propertyEntry) {
        if (isLock()) {
            AbstractLockablePropertyMap.lockError();
            return false;
        }
        if (contain(propertyEntry))
            return false;
        this.array = Arrays.copyOf(this.array, size() + 1);
        this.array[size() - 1] = AbstractLockablePropertyMap.LockableSinglePropertyEntry.asLockable(propertyEntry);
        return true;
    }

    @Override
    public boolean remove(SinglePropertyEntry<?> propertyEntry) {
        if (isLock()) {
            AbstractLockablePropertyMap.lockError();
            return false;
        }
        if (!contain(propertyEntry))
            return false;
        List<SinglePropertyEntry<?>> list = Arrays.asList(this.array);
        return remove(list.indexOf(AbstractLockablePropertyMap.LockableSinglePropertyEntry.asLockable(propertyEntry)));
    }

    @Override
    public boolean remove(int index) {
        if (isLock()) {
            AbstractLockablePropertyMap.lockError();
            return false;
        }
        if (index >= this.array.length) {
            try {
                throw new ArrayIndexOutOfBoundsException(index);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }
        List<AbstractLockablePropertyMap.LockableSinglePropertyEntry<?>> list = Arrays.asList(this.array);
        list.remove(index);
        this.array = list.toArray(Arrays.copyOf(this.array, list.size()));
        return true;
    }

    private boolean containKey(String key) {
        return Arrays.stream(this.array).anyMatch(propertyEntry -> propertyEntry.key().equals(key));
    }

    @Override
    public @NotNull Iterator<SinglePropertyEntry<?>> iterator() {
        return new PropertyEntriesImpl.IteratorImpl<>(this.array.clone());
    }

    protected void lock() {
        this.lock = true;
    }

    private boolean isLock() {
        return this.lock;
    }

    @Override
    public String toString() {
        return "PropertyEntry{" +
                "array=" + Arrays.toString(array) +
                '}';
    }

    private static class LockableTypedPropertyEntries<T> implements TypedPropertyEntries<T> {

        private final Class<T> type;

        private AbstractLockablePropertyMap.LockableSinglePropertyEntry<T>[] array =
                new AbstractLockablePropertyMap.LockableSinglePropertyEntry[0];
        private boolean lock;

        private LockableTypedPropertyEntries(Class<T> type) {
            this.type = type;
            this.lock = false;
        }

        private void add(SinglePropertyEntry<T> entry) {
            if (this.lock) {
                AbstractLockablePropertyMap.lockError();
                return;
            }
            this.array = Arrays.copyOf(this.array, size() + 1);
            this.array[size() - 1] = AbstractLockablePropertyMap.LockableSinglePropertyEntry.asLockable(entry);
        }

        @Override
        public int size() {
            return this.array.length;
        }

        @Override
        public boolean contain(SinglePropertyEntry<T> propertyEntry) {
            return Arrays.asList(this.array).contains(AbstractLockablePropertyMap.LockableSinglePropertyEntry
                    .asLockable(propertyEntry));
        }

        @Override
        public SinglePropertyEntry<T> get(String key) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(key, this.type);
            Optional<AbstractLockablePropertyMap.LockableSinglePropertyEntry<T>> optional =
                    Arrays.stream(this.array).filter(propertyEntry -> propertyEntry.key().equals(key)).findFirst();
            if (!optional.isPresent())
                throw new MissingPropertyException(key, type);
            return optional.get();
        }

        @Override
        public @NotNull Iterator<SinglePropertyEntry<T>> iterator() {
            return new PropertyEntriesImpl.IteratorImpl<>(this.array);
        }

        private boolean containKey(String key) {
            return Arrays.stream(this.array).anyMatch(propertyEntry -> propertyEntry.key().equals(key));
        }

        private void lock() {
            this.lock = true;
        }

        public boolean isLock() {
            return this.lock;
        }

        @Override
        public String toString() {
            return "TypedPropertyEntry{" +
                    "type=" + type +
                    ", array=" + Arrays.toString(array) +
                    '}';
        }
    }
}
