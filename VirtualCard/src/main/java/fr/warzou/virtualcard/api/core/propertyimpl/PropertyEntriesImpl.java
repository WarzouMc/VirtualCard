package fr.warzou.virtualcard.api.core.propertyimpl;

import fr.warzou.virtualcard.utils.property.PropertyEntries;
import fr.warzou.virtualcard.utils.property.SinglePropertyEntry;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PropertyEntriesImpl implements PropertyEntries {

    private SinglePropertyEntry<?>[] array = new SinglePropertyEntry<?>[0];

    @Override
    public int size() {
        return this.array.length;
    }

    @Override
    public boolean contain(SinglePropertyEntry<?> propertyEntry) {
        return Arrays.asList(this.array).contains(propertyEntry);
    }

    @Override
    public <T> TypedPropertyEntries<T> filter(Class<T> type) {
        TypedPropertyEntriesImpl<T> typedEntry = new TypedPropertyEntriesImpl<>(type);
        for (SinglePropertyEntry<?> propertyEntry : this)
            if (type.isAssignableFrom(propertyEntry.type()))
                typedEntry.add((SinglePropertyEntry<T>) propertyEntry);
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
        return Arrays.stream(this.array).filter(propertyEntry -> propertyEntry.key().equals(key)).findFirst().get();
    }

    @Override
    public boolean add(SinglePropertyEntry<?> propertyEntry) {
        if (contain(propertyEntry))
            return false;
        this.array = Arrays.copyOf(this.array, size() + 1);
        this.array[size() - 1] = propertyEntry;
        return true;
    }

    @Override
    public boolean remove(SinglePropertyEntry<?> propertyEntry) {
        if (!contain(propertyEntry))
            return false;
        List<SinglePropertyEntry<?>> list = Arrays.asList(this.array);
        return remove(list.indexOf(propertyEntry));
    }

    @Override
    public boolean remove(int index) {
        if (index >= this.array.length) {
            try {
                throw new ArrayIndexOutOfBoundsException(index);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }
        List<SinglePropertyEntry<?>> list = Arrays.asList(this.array);
        list.remove(index);
        this.array = list.toArray(Arrays.copyOf(this.array, list.size()));
        return true;
    }

    private boolean containKey(String key) {
        return Arrays.stream(this.array).anyMatch(propertyEntry -> propertyEntry.key().equals(key));
    }

    @Override
    public @NotNull Iterator<SinglePropertyEntry<?>> iterator() {
        return new IteratorImpl<>(this.array.clone());
    }

    @Override
    public String toString() {
        return "PropertyEntry{" +
                "array=" + Arrays.toString(array) +
                '}';
    }

    private static class TypedPropertyEntriesImpl<T> implements TypedPropertyEntries<T> {

        private final Class<T> type;

        private SinglePropertyEntry<T>[] array = new SinglePropertyEntry[0];

        private TypedPropertyEntriesImpl(Class<T> type) {
            this.type = type;
        }

        private void add(SinglePropertyEntry<T> entry) {
            this.array = Arrays.copyOf(this.array, size() + 1);
            this.array[size() - 1] = entry;
        }

        @Override
        public int size() {
            return this.array.length;
        }

        @Override
        public boolean contain(SinglePropertyEntry<T> propertyEntry) {
            return Arrays.asList(this.array).contains(propertyEntry);
        }

        @Override
        public SinglePropertyEntry<T> get(String key) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(key, this.type);
            Optional<SinglePropertyEntry<T>> optional =
                    Arrays.stream(this.array).filter(propertyEntry -> propertyEntry.key().equals(key)).findFirst();
            if (!optional.isPresent())
                throw new MissingPropertyException(key, this.type);
            return optional.get();
        }

        @Override
        public @NotNull java.util.Iterator<SinglePropertyEntry<T>> iterator() {
            return new IteratorImpl<>(this.array);
        }

        private boolean containKey(String key) {
            return Arrays.stream(this.array).anyMatch(propertyEntry -> propertyEntry.key().equals(key));
        }

        @Override
        public String toString() {
            return "TypedPropertyEntry{" +
                    "type=" + type +
                    ", array=" + Arrays.toString(array) +
                    '}';
        }
    }

    protected static class IteratorImpl<E> implements Iterator<E> {

        private final E[] array;

        private int target;

        protected IteratorImpl(E[] array) {
            this.array = array;
            this.target = 0;
        }

        @Override
        public boolean hasNext() {
            return this.target < this.array.length;
        }

        @Override
        public E next() {
            try {
                E element = this.array[this.target];
                this.target++;
                return element;
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
