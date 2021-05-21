package fr.warzou.virtualcard.core.modules.pusher;

import fr.warzou.virtualcard.api.environment.EnvironmentComponent;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractPropertyMap;
import fr.warzou.virtualcard.api.events.events.pusher.PusherPushEvent;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.Updatable;
import fr.warzou.virtualcard.core.modules.container.AbstractContainer;
import fr.warzou.virtualcard.core.modules.link.LinkedModule;
import fr.warzou.virtualcard.core.modules.link.Point;
import fr.warzou.virtualcard.utils.event.EventsManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Pusher extends EnvironmentComponent<ModuleIOStream> implements Updatable {

    protected final PropertyMap propertyMap;
    protected AbstractModuleFile moduleFile;
    protected ModuleIOStream stream;

    protected Pusher() {
        this.propertyMap = new PusherPropertyMap(this);
    }

    public static Pusher getDefaultPusher() {
        return new DefaultPusher();
    }

    public abstract AbstractContainer linkTo();

    public abstract ArrayList getTasks();

    public abstract boolean addTask(int taskBound);

    public abstract boolean removeTask(int id);

    public abstract int getTaskBound(int id);

    public abstract void update();

    protected abstract void push();

    protected void createNewIndex(ArrayList array) {
        ArrayList arrayList = new ArrayList(array);
        array = new ArrayList(arrayList.size() + 1);
        array.addAll(arrayList);
        array.add(null);
    }

    public static abstract class AbstractIndexedPusher extends Pusher {

        protected final String name;
        protected final String path;

        protected AbstractIndexedPusher(String subName, String path) {
            super();
            this.name = "pusher" + (subName != null && !subName.isEmpty() ? "." + subName : "");
            this.path = path == null || path.isEmpty() || !path.endsWith(".json") ? "modules/pusher/pusher.json"
                    : path;
            this.propertyMap.put("name", this.name);
            this.stream = new IOStream(this);
        }

        @Override
        public String moduleName() {
            return this.name;
        }

        @Override
        public ArrayList getTasks() {
            Reader<ArrayList> reader = this.stream.readArray(this.name + ".out.push.tasks");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            return optional.orElseGet(ArrayList::new);
        }

        @Override
        public boolean addTask(int taskBound) {
            ArrayList currentTasks = getTasks();
            if (currentTasks == null)
                return false;
            for (Object bound : currentTasks) {
                if (bound.equals(taskBound))
                    return false;
            }

            createNewIndex(currentTasks);
            currentTasks.add(taskBound);

            Writer<ArrayList> writer = getStream().write(this.name + ".in.push.tasks", ArrayList.class);
            writer.write(currentTasks);
            writer.push();
            writer.close();
            return true;
        }

        @Override
        public boolean removeTask(int id) {
            ArrayList tasks = getTasks();
            if (tasks == null || tasks.size() <= id)
                return false;
            tasks.remove(id);
            Writer<ArrayList> writer = getStream().write(this.name + ".in.push.tasks", ArrayList.class);
            writer.write(tasks);
            writer.push();
            writer.close();
            return true;
        }

        @Override
        public int getTaskBound(int id) {
            ArrayList tasks = getTasks();
            if (tasks == null || tasks.size() <= id)
                return -1;
            return Math.toIntExact(Math.round(Double.parseDouble(tasks.get(id).toString())));
        }

        @Override
        public ModuleIOStream getStream() {
            if (this.stream != null)
                return this.stream;
            return this.stream = new IOStream(this);
        }

        @Override
        public PropertyMap getProperties() {
            return this.propertyMap;
        }

        @Override
        public AbstractModuleFile file() {
            if (this.moduleFile != null)
                return this.moduleFile;
            return this.moduleFile = new AbstractModuleFile(this) {
                @Override
                public @NotNull String mainFile() {
                    return path;
                }
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            AbstractIndexedPusher that = (AbstractIndexedPusher) o;
            return that.name != null && this.name != null && that.name.equals(this.name);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (path != null ? path.hashCode() : 0);
            return result;
        }
    }

    private static class DefaultPusher extends AbstractIndexedPusher {

        protected DefaultPusher() {
            super(null, null);
        }

        @Override
        public AbstractContainer linkTo() {
            Reader<String> reader = this.stream.readString(this.name + ".out.stats.link");
            Optional<String> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return null;
            Optional<ModuleBase<?>> optionalModuleBase = this.card.getModuleManager().getModule(optional.get());
            if (!optionalModuleBase.isPresent())
                return null;
            ModuleBase<?> moduleBase = optionalModuleBase.get();
            if (!(moduleBase instanceof AbstractContainer))
                return null;
            return (AbstractContainer) moduleBase;
        }

        @Override
        public void update() {
            ArrayList tasks = getTasks();
            int removeTask = -1;
            for (int i = 0; i < tasks.size(); i++) {
                int task = Math.toIntExact(Math.round(Double.parseDouble(tasks.get(i).toString())));
                if (task == 0) {
                    removeTask = i;
                    continue;
                }
                tasks.set(i, task--);
            }
            if (removeTask >= 0) {
                push();
                removeTask(removeTask);
            }
            Writer<ArrayList> writer = this.stream.write(this.name + ".in.push.tasks", ArrayList.class);
            writer.write(tasks);
            writer.push();
            writer.close();
        }

        @Override
        protected void push() {
            AbstractContainer container = linkTo();
            if (container == null)
                return;
            Optional<LinkedModule> optionalLinkedModule = container.linkedModuleFromModule(this);
            if (!optionalLinkedModule.isPresent())
                return;
            LinkedModule linkedModule = optionalLinkedModule.get();
            Point thisPoint = linkedModule.getPoint();
            Point frontPoint = thisPoint.inFront();
            Optional<LinkedModule> optionalFrontLinkedModule = container.linkedModuleFromPoint(frontPoint);
            if (!optionalFrontLinkedModule.isPresent())
                return;
            LinkedModule frontModule = optionalFrontLinkedModule.get();
            ModuleBase<?> moduleBase = frontModule.getModuleBase();
            if (!(moduleBase instanceof AbstractContainer))
                return;
            AbstractContainer frontContainer = (AbstractContainer) moduleBase;
            List<Item<?>> content = container.content();
            int pushPosition = thisPoint.position();
            if (pushPosition >= content.size())
                return;
            Item<?> item = content.get(pushPosition - 1);
            if (item.getItem() == null)
                return;
            frontContainer.inject(item);
            container.eject(item);
            EventsManager eventsManager = this.card.getEventManager();
            eventsManager.callEvent(new PusherPushEvent(this, item, container, frontContainer));
        }
    }

    private static class PusherPropertyMap extends AbstractPropertyMap {

        private final Pusher pusher;

        private PusherPropertyMap(Pusher pusher) {
            this.pusher = pusher;
        }

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(this.pusher, key);

            TypedPropertyEntries<T> entry = this.entries.filter(clazz);
            return entry.get(key).value();
        }
    }

    private static class IOStream implements ModuleIOStream {

        private final Pusher pusher;
        private final PropertyMap packetMap;

        private IOStream(Pusher pusher) {
            this.pusher = pusher;
            this.packetMap = new PacketParser(pusher.file()).parse();
        }

        @Override
        public <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new NoWriter<>(key, type);

            PacketIn packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.pusher.card.getModuleManager(), key,
                        new PacketSource(this.pusher.getClass(), this.pusher.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.INPUT.ordinal())
                    return new NoWriter<>(key, type);
                packet = (PacketIn) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new NoWriter<>(key, type);
            }
            return PacketDispatcher.inDispatch(this.pusher.card, packet, type);
        }

        @Override
        public <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new EmptyReader<>(key, type);

            PacketOut packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.pusher.card.getModuleManager(), key,
                        new PacketSource(this.pusher.getClass(), this.pusher.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.OUTPUT.ordinal())
                    return new EmptyReader<>(key, type);
                packet = (PacketOut) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new EmptyReader<>(key, type);
            }
            return PacketDispatcher.outDispatch(this.pusher.card, packet, type);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.packetMap;
        }
    }
}
