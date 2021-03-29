package fr.warzou.virtualcard.api.environment.property;

import fr.warzou.virtualcard.api.environment.path.PropertyEntry;
import fr.warzou.virtualcard.api.environment.path.SinglePropertyEntry;
import fr.warzou.virtualcard.api.environment.path.TypedPropertyEntry;
import fr.warzou.virtualcard.utils.exception.property.MissingPropertyException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PropertyEntryImpl implements PropertyEntry {

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
    public <T> TypedPropertyEntry<T> filter(Class<T> type) {
        TypedPropertyEntryImpl<T> typedEntry = new TypedPropertyEntryImpl<>(type);
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
                throw new NoSuchElementException();
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
        return false;
    }

    @Override
    public boolean remove(int index) {
        return false;
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

    private static class TypedPropertyEntryImpl<T> implements TypedPropertyEntry<T> {

        private final Class<T> type;

        private SinglePropertyEntry<T>[] array = new SinglePropertyEntry[0];

        private TypedPropertyEntryImpl(Class<T> type) {
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
            return Arrays.stream(this.array).filter(propertyEntry -> propertyEntry.key().equals(key)).findFirst().get();
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
                throw new NoSuchElementException();
            }
        }
    }

}
