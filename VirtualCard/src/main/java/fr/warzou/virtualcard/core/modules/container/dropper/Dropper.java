package fr.warzou.virtualcard.core.modules.container.dropper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import fr.warzou.virtualcard.utils.module.ModuleBase;
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

import java.util.*;

public abstract class Dropper extends AbstractContainer implements Iterable<Item<?>> {

    protected final PropertyMap propertyMap;

    public Dropper() {
        this.propertyMap = new DropperPropertyMap(this);
        this.propertyMap.put("name", "dropper");
    }

    public static Dropper defaultDropper() {
        return new DefaultDropper();
    }

    public abstract boolean fill(@NotNull Item<?>[] elements);

    public abstract boolean add(@NotNull Item<?> element);

    public abstract boolean remove();

    public List<Item<?>> content() {
        return queue();
    }

    public abstract ArrayList<Item<?>> queue();

    public abstract int capacity();

    public abstract int emptySlot();

    public abstract void setCapacity(int capacity);

    public abstract int currentSize();

    public abstract int updateTick();

    public abstract AbstractContainer injectModule();

    public abstract AbstractContainer ejectModule();

    protected abstract boolean drop();

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

    private static class DefaultDropper extends Dropper {

        private AbstractModuleFile moduleFile;
        private ModuleIOStream stream;

        public DefaultDropper() {
            this.stream = new IOStream(this);
        }

        @Override
        public boolean fill(@NotNull Item<?>[] elements) {
            int capacity = capacity();
            if (elements.length >= capacity)
                return false;
            Writer<Integer> writer = getStream().writeInt("dropper.in.queue.empty_slot");
            writer.write(capacity - elements.length);
            writer.push();
            writer.close();

            ArrayList<Item<?>> list = new ArrayList<>(Arrays.asList(elements));
            ArrayList<Object> array = new ArrayList<>(list.size());
            list.forEach(item -> array.add(item.getItem()));
            Writer<ArrayList> queueWriter = getStream().write("dropper.in.queue.queue", ArrayList.class);
            queueWriter.write(array);
            queueWriter.push();
            queueWriter.close();
            return false;
        }

        @Override
        public boolean add(@NotNull Item<?> element) {
            return set(element, true);
        }

        @Override
        public boolean remove() {
            return set(null, false);
        }

        @Override
        public ArrayList<Item<?>> queue() {
            ArrayList<Item<?>> list = new ArrayList<>();
            IteratorImpl iterator = (IteratorImpl) iterator();
            while (iterator.hasNext()) {
                Item<?> next = iterator.next();
                list.add(next);
            }
            return list;
        }

        @Override
        public int capacity() {
            Reader<Integer> reader = getStream().read("dropper.out.queue.max_queue", Integer.class);
            Optional<Integer> optional = reader.read();
            reader.close();
            return optional.orElse(-1);
        }

        @Override
        public int emptySlot() {
            Reader<Integer> reader = getStream().read("dropper.out.queue.empty_slot", Integer.class);
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

            Writer<Integer> writer = this.getStream().writeInt("dropper.in.queue.max_queue");
            writer.write(capacity);
            writer.push();
            writer.close();

            writer = this.getStream().writeInt("dropper.in.queue.empty_slot");
            writer.write(newEmptySlot);
            writer.push();
            writer.close();
        }

        @Override
        public int currentSize() {
            return capacity() - emptySlot();
        }

        @Override
        public int updateTick() {
            Reader<Integer> reader = getStream().read("dropper.out.stats.update_ticks", int.class);
            Optional<Integer> optional = reader.read();
            reader.close();
            return optional.orElse(20);
        }

        @Override
        public AbstractContainer injectModule() {
            Reader<String> reader = this.stream.readString("dropper.out.queue.inject");
            Optional<String> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return null;
            String moduleName = optional.get();
            Optional<ModuleBase<?>> moduleBaseOptional = this.card.getModuleManager().getModule(moduleName);
            if (!moduleBaseOptional.isPresent())
                return null;
            ModuleBase<?> moduleBase = moduleBaseOptional.get();
            if (!(moduleBase instanceof AbstractContainer))
                return null;
            return (AbstractContainer) moduleBase;
        }

        @Override
        public AbstractContainer ejectModule() {
            Reader<String> reader = this.stream.readString("dropper.out.queue.eject");
            Optional<String> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return null;
            String moduleName = optional.get();
            Optional<ModuleBase<?>> moduleBaseOptional = this.card.getModuleManager().getModule(moduleName);
            if (!moduleBaseOptional.isPresent())
                return null;
            ModuleBase<?> moduleBase = moduleBaseOptional.get();
            if (!(moduleBase instanceof AbstractContainer))
                return null;
            return (AbstractContainer) moduleBase;
        }

        @Override
        protected boolean drop() {
            List<Item<?>> queue = queue();
            AbstractContainer abstractContainer = ejectModule();
            if (abstractContainer == null)
                return false;
            if (queue.size() == 0)
                return abstractContainer.inject(new Item<>(Object.class, null));
            Item<?> item = queue.get(0);
            if (!remove())
                return abstractContainer.inject(new Item<>(Object.class, null));
            return abstractContainer.inject(item);
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
                    return "modules/dropper/dropper.json";
                }
            };
            return this.moduleFile;
        }

        private boolean set(Item<?> item, boolean add) {
            int currentSize = currentSize();
            try {
                if (currentSize >= capacity())
                    throw new IndexOutOfBoundsException(currentSize + " > " + (capacity() - 1));
                if (currentSize < 0)
                    throw new IndexOutOfBoundsException(currentSize + " < 0");
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }

            Reader<ArrayList> reader = getStream().readArray("dropper.out.queue.queue");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return false;

            ArrayList array = optional.get();
            if (add) {
                createNewIndex(array);
                Writer<Integer> writer = getStream().writeInt("dropper.in.queue.empty_slot");
                writer.write(emptySlot() - 1);
                writer.push();
                writer.close();
            } else {
                if (array.size() == 0)
                    return false;
                array.remove(0);
                Writer<Integer> writer = getStream().writeInt("dropper.in.queue.empty_slot");
                writer.write(emptySlot() + 1);
                writer.push();
                writer.close();
                Writer<ArrayList> arrayWriter = getStream().write("dropper.in.queue.queue", ArrayList.class);
                arrayWriter.write(array);
                arrayWriter.push();
                arrayWriter.close();
                return true;
            }

            JsonElement jsonElement = JsonParser.parseString(new Gson().toJson(item.getItem()));
            array.set(currentSize - 1, jsonElement);

            Writer<ArrayList> writer = getStream().write("dropper.in.queue.queue", ArrayList.class);
            writer.write(array);
            writer.push();
            writer.close();
            return true;
        }

        @Override
        public boolean inject(Item<?> item) {
            return false;
        }

        @Override
        public boolean eject(Item<?> item) {
            return drop();
        }

        @Override
        public LinkedModule[] links() {
            return new LinkedModule[] {new LinkedModule(injectModule(), new Point(LinkedModule.Face.UP, 0)),
                    new LinkedModule(ejectModule(), new Point(LinkedModule.Face.DOWN, 0))};
        }

        @Override
        public int[] size() {
            return new int[] {0, 0, 0, 0, 1, 1};
        }

        @Override
        public Point[] linkablePoint() {
            return new Point[] {new Point(LinkedModule.Face.UP, 0), new Point(LinkedModule.Face.DOWN, 0)};
        }
    }

    private static class DropperPropertyMap extends AbstractPropertyMap {

        private final Dropper dropper;

        private DropperPropertyMap(Dropper dropper) {
            this.dropper = dropper;
        }

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(this.dropper, key);

            TypedPropertyEntries<T> entry = this.entries.filter(clazz);
            return entry.get(key).value();
        }
    }

    private static class IOStream implements ModuleIOStream {

        private final Dropper dropper;
        private final PropertyMap packetMap;

        private IOStream(Dropper dropper) {
            this.dropper = dropper;
            this.packetMap = new PacketParser(this.dropper.file()).parse();
        }

        @Override
        public <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new NoWriter<>(key, type);

            PacketIn packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.dropper.card.getModuleManager(), key,
                        new PacketSource(this.dropper.getClass(), this.dropper.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.INPUT.ordinal())
                    return new NoWriter<>(key, type);
                packet = (PacketIn) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new NoWriter<>(key, type);
            }
            return PacketDispatcher.inDispatch(this.dropper.card, packet, type);
        }

        @Override
        public <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new EmptyReader<>(key, type);

            PacketOut packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.dropper.card.getModuleManager(), key,
                        new PacketSource(this.dropper.getClass(), this.dropper.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.OUTPUT.ordinal())
                    return new EmptyReader<>(key, type);
                packet = (PacketOut) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new EmptyReader<>(key, type);
            }
            return PacketDispatcher.outDispatch(this.dropper.card, packet, type);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.packetMap;
        }
    }

    private static class IteratorImpl implements Iterator<Item<?>> {

        private final Dropper dropper;
        private final Item<?>[] content;
        private int count = 0;

        private IteratorImpl(Dropper dropper) {
            this.dropper = dropper;
            this.content = new Item[this.dropper.capacity()];
            Reader<ArrayList> reader = this.dropper.getStream().readArray("dropper.out.queue.queue");
            Optional<ArrayList> optional = reader.read();
            if (!optional.isPresent())
                return;
            ArrayList list = optional.get();
            Gson gson = new Gson();
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                Object object = gson.fromJson(element.toString(), Object.class);
                this.content[i] = new Item<>(Object.class, object);
            }
        }

        public Item<?> get(int index) {
            int count = 0;
            while (hasNext()) {
                if (index - 1 == count)
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
