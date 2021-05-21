package fr.warzou.virtualcard.core.modules.container.container;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.environment.EnvironmentComponent;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractPropertyMap;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.container.AbstractContainer;
import fr.warzou.virtualcard.core.modules.link.LinkedModule;
import fr.warzou.virtualcard.core.modules.link.Point;
import fr.warzou.virtualcard.exception.packet.PacketNameException;
import fr.warzou.virtualcard.exception.packet.PacketNameInterpreterException;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.file.AbstractModuleFile;
import fr.warzou.virtualcard.utils.module.packets.PacketSource;
import fr.warzou.virtualcard.utils.module.packets.packet.*;
import fr.warzou.virtualcard.utils.module.stream.ModuleIOStream;
import fr.warzou.virtualcard.utils.module.stream.ModuleStreamType;
import fr.warzou.virtualcard.utils.module.stream.executor.impl.EmptyReader;
import fr.warzou.virtualcard.utils.module.stream.executor.impl.NoWriter;
import fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader;
import fr.warzou.virtualcard.utils.module.stream.executor.writer.Writer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class Container extends AbstractContainer implements Iterable<Item<?>> {

    protected final PropertyMap propertyMap;

    public Container() {
        this.propertyMap = new ContainerPropertyMap(this);
    }

    public static Container defaultContainer() {
        return new DefaultContainer();
    }

    public abstract boolean add(@NotNull Item<?> element);

    public abstract boolean remove(@NotNull Item<?> element);

    public abstract boolean remove(int index);

    public abstract List<Item<?>> content();

    public abstract int capacity();

    public abstract int emptySlot();

    public abstract void setCapacity(int capacity);

    @NotNull
    @Override
    public Iterator<Item<?>> iterator() {
        return new IteratorImpl(this);
    }

    @Override
    public String moduleName() {
        try {
            return this.propertyMap.getProperty("name", String.class).value();
        } catch (MissingPropertyException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void createNewIndex(ArrayList array) {
        int size = array.size();
        if (size < capacity()) {
            ArrayList arrayList = new ArrayList(array);
            array = new ArrayList(arrayList.size() + 1);
            array.addAll(arrayList);
            array.add(null);
            return;
        }
        throw new IndexOutOfBoundsException((size + 1) + " is to big !");
    }

    private static abstract class AbstractDefaultContainer extends Container {

        private final String name;
        private final String path;
        private AbstractModuleFile moduleFile;
        private ModuleIOStream stream;

        public AbstractDefaultContainer(String sub, String path) {
            super();
            this.name = "container" + (sub != null && !sub.isEmpty() ? "." + sub : "");
            this.path = path == null || path.isEmpty() || !path.endsWith(".json") ? "modules/container/container.json"
                    : path;
            this.propertyMap.put("name", this.name);
            this.stream = new IOStream(this);
        }

        @Override
        public boolean add(@NotNull Item<?> element) {
            int slot = capacity() - emptySlot();
            return set(slot, element, true, false);
        }

        public boolean set(int index, @NotNull Item<?> item) {
            return set(index, item, false, false);
        }

        @Override
        public boolean remove(@NotNull Item<?> item) {
            List<Item<?>> list = this.content();
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                Item<?> check = list.get(i);
                if (!check.equals(item))
                    continue;
                index = i;
                break;
            }
            return remove(index);
        }

        @Override
        public boolean remove(int index) {
            return set(index, null, false, true);
        }

        @Override
        public List<Item<?>> content() {
            ArrayList<Item<?>> list = new ArrayList<>();
            this.forEach(item -> {
                if (item != null) list.add(item);
            });
            return list;
        }

        @Override
        public int capacity() {
            Reader<Integer> reader = getStream().read(this.name + ".out.content.capacity", Integer.class);
            Optional<Integer> optional = reader.read();
            reader.close();
            return optional.orElse(-1);
        }

        @Override
        public int emptySlot() {
            Reader<Integer> reader = getStream().read(this.name + ".out.content.empty_slot", Integer.class);
            Optional<Integer> optional = reader.read();
            reader.close();
            return optional.orElse(-1);
        }

        @Override
        public void setCapacity(int capacity) {
            int oldCapacity = capacity();
            int tookSlot = oldCapacity - emptySlot();
            capacity = Math.abs(capacity);
            if (capacity == 0)
                return;

            int newEmptySlot = capacity - tookSlot;
            if (newEmptySlot < 0)
                return;

            Writer<Integer> writer = this.getStream().writeInt(this.name + ".in.content.capacity");
            writer.write(capacity);
            writer.push();
            writer.close();

            writer = this.getStream().writeInt(this.name + ".in.content.empty_slot");
            writer.write(newEmptySlot);
            writer.push();
            writer.close();
        }

        @Override
        public ModuleIOStream getStream() {
            if (this.stream != null)
                return this.stream;
            this.stream = new IOStream(this);
            return this.stream;
        }

        @Override
        public PropertyMap getProperties() {
            return this.propertyMap;
        }

        @Override
        public AbstractModuleFile file() {
            if (this.moduleFile != null)
                return this.moduleFile;
            this.moduleFile = new AbstractModuleFile(this) {
                @Override
                public @NotNull String mainFile() {
                    return path;
                }
            };
            return this.moduleFile;
        }

        private boolean set(int index, Item<?> item, boolean add, boolean remove) {
            try {
                if (index >= capacity())
                    throw new IndexOutOfBoundsException(index + " > " + (capacity() - 1));
                if (index < 0)
                    throw new IndexOutOfBoundsException(index + " < 0");
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }

            Reader<ArrayList> reader = getStream().readArray(this.name + ".out.content.content");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return false;

            ArrayList array = optional.get();
            if (add) {
                createNewIndex(array);
                Writer<Integer> writer = getStream().writeInt(this.name + ".in.content.empty_slot");
                writer.write(emptySlot() - 1);
                writer.push();
                writer.close();
            }

            if (remove) {
                if (index >= array.size()) {
                    try {
                        throw new IndexOutOfBoundsException(index + " > " + (array.size() - 1));
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                array.remove(index);
                Writer<Integer> writer = getStream().writeInt(this.name + ".in.content.empty_slot");
                writer.write(emptySlot() + 1);
                writer.push();
                writer.close();
                Writer<ArrayList> arrayWriter = getStream().write(this.name + ".in.content.content", ArrayList.class);
                arrayWriter.write(array);
                arrayWriter.push();
                arrayWriter.close();
                return true;
            }

            array.add(item.getItem());

            Writer<ArrayList> writer = getStream().write(this.name + ".in.content.content", ArrayList.class);
            writer.write(array);
            writer.push();
            writer.close();
            return true;
        }
    }

    private static class DefaultContainer extends AbstractDefaultContainer {

        private DefaultContainer() {
            super(null, null);
        }

        @Override
        public boolean inject(Item<?> item) {
            return add(item);
        }

        @Override
        public boolean eject(Item<?> item) {
            return false;
        }

        @Override
        public LinkedModule[] links() {
            return new LinkedModule[] {};
        }

        @Override
        public int[] size() {
            return new int[] {0, 0, 0, 0, 0, 0};
        }

        @Override
        public Point[] linkablePoint() {
            return new Point[] {};
        }
    }

    public static abstract class AbstractIndexedContainer extends AbstractDefaultContainer {

        public AbstractIndexedContainer(@NotNull String sub, @NotNull String path) {
            super(sub, path);
        }

        @Override
        public @Nullable EnvironmentComponent<ModuleIOStream> init(Card card) {
            return super.init(card);
        }
    }

    private static class ContainerPropertyMap extends AbstractPropertyMap {

        private final Container container;

        private ContainerPropertyMap(Container container) {
            this.container = container;
        }

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(this.container, key);

            TypedPropertyEntries<T> entry = this.entries.filter(clazz);
            return entry.get(key).value();
        }
    }

    private static class IOStream implements ModuleIOStream {

        private final Container container;
        private final PropertyMap packetMap;

        private IOStream(Container container) {
            this.container = container;
            this.packetMap = new PacketParser(this.container.file()).parse();
        }

        @Override
        public <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new NoWriter<>(key, type);

            PacketIn packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.container.card.getModuleManager(), key,
                        new PacketSource(this.container.getClass(), this.container.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.INPUT.ordinal())
                    return new NoWriter<>(key, type);
                packet = (PacketIn) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new NoWriter<>(key, type);
            }
            return PacketDispatcher.inDispatch(this.container.card, packet, type);
        }

        @Override
        public <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new EmptyReader<>(key, type);

            PacketOut packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.container.card.getModuleManager(), key,
                        new PacketSource(this.container.getClass(), this.container.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.OUTPUT.ordinal())
                    return new EmptyReader<>(key, type);
                packet = (PacketOut) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new EmptyReader<>(key, type);
            }
            return PacketDispatcher.outDispatch(this.container.card, packet, type);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.packetMap;
        }
    }

    private static class IteratorImpl implements Iterator<Item<?>> {

        private final Container container;
        private final Item<?>[] content;
        private int count = 0;

        private IteratorImpl(Container container) {
            this.container = container;
            this.content = new Item[this.container.capacity()];
            Reader<ArrayList> reader = this.container.getStream().readArray(this.container.moduleName() + ".out.content.content");
            Optional<ArrayList> optional = reader.read();
            if (!optional.isPresent())
                return;
            ArrayList list = optional.get();
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                this.content[i] = new Item<>(Object.class, element);
            }
        }

        public Item<?> get(int index) {
            int count = 0;
            while (hasNext()) {
                if (index == count)
                    return next();
                next();
                count++;
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return this.count < this.content.length;
        }

        @Override
        public Item<?> next() {
            if (!hasNext())
                return null;
            Item<?> item = this.content[this.count];
            this.count++;
            return item;
        }
    }
}
