package fr.warzou.virtualcard.core.modules;

public class Item<E> {

    protected final Class<E> type;
    protected final E item;

    public Item(Class<E> type, E item) {
        this.type = type;
        this.item = item;
    }

    public E getItem() {
        return this.item;
    }

    public Class<E> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item<?> item1 = (Item<?>) o;

        if (!type.equals(item1.type)) return false;
        return item.equals(item1.item);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + item.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "type=" + type +
                ", item=" + item +
                '}';
    }
}
