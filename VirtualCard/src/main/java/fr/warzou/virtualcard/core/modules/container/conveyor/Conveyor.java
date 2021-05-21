package fr.warzou.virtualcard.core.modules.container.conveyor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.api.core.ticktask.CardRunnable;
import fr.warzou.virtualcard.api.core.ticktask.CardTask;
import fr.warzou.virtualcard.api.environment.EnvironmentComponent;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractPropertyMap;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.Updatable;
import fr.warzou.virtualcard.core.modules.captor.Captor;
import fr.warzou.virtualcard.core.modules.container.AbstractContainer;
import fr.warzou.virtualcard.core.modules.link.UniqueCaptorLinkable;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Conveyor extends AbstractContainer implements UniqueCaptorLinkable {

    protected final PropertyMap propertyMap;

    protected Conveyor() {
        this.propertyMap = new ConveyorPropertyMap(this);
        this.propertyMap.put("name", "conveyor");
    }

    public static Conveyor createConveyor() {
        return new DefaultConveyor();
    }

    public abstract int length();

    public abstract boolean putItem(Item<?> item);

    public List<Item<?>> content() {
        ConveyorItem<?>[] rawContent = rawContent();
        return Arrays.asList(rawContent);
    }

    public abstract ConveyorItem<?>[] rawContent();

    public abstract AbstractContainer itemSource();

    public abstract AbstractContainer tailEnd();

    public abstract String[] linkedModulesName();

    public abstract LinkedModule[] linkedModules();

    public abstract int updateTicks();

    protected abstract void update();

    @Override
    public String moduleName() {
        return "conveyor";
    }

    @Override
    public PropertyMap getProperties() {
        return this.propertyMap;
    }

    private static class DefaultConveyor extends Conveyor {

        private CardTask task;
        private AbstractModuleFile moduleFile;
        private ModuleIOStream stream;

        private boolean accept = true;
        private Point[] linkablePoint;

        protected DefaultConveyor() {
            this.stream = getStream();
        }

        @Override
        public @Nullable EnvironmentComponent<ModuleIOStream> init(Card card) {
            EnvironmentComponent<ModuleIOStream> component = super.init(card);
            int update = updateTicks();
            this.task = new ConveyorUpdater(this).runTaskTimer(this.card, 20, update);
            return component;
        }

        @Override
        public int length() {
            Reader<Integer> reader = this.stream.readInt("conveyor.out.stats.length");
            Optional<Integer> optional = reader.read();
            reader.close();
            return optional.orElse(-1);
        }

        @Override
        public boolean putItem(Item<?> item) {
            return inject(item);
        }

        @Override
        public ConveyorItem<?>[] rawContent() {
            int length = length();
            if (length < 0)
                return new ConveyorItem<?>[0];
            Reader<ArrayList> reader = this.stream.readArray("conveyor.out.view.content");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return new ConveyorItem<?>[length];
            ArrayList list = optional.get();
            ConveyorItem<?>[] items = new ConveyorItem<?>[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Object object = list.get(i);
                Item<?> item = new Item<>(Object.class, object);
                ConveyorItem<?> conveyorItem = ConveyorItem.fromItem(item);
                conveyorItem.position = i;
                items[i] = conveyorItem;
            }
            return items;
        }

        @Override
        public AbstractContainer itemSource() {
            Reader<String> reader = this.stream.readString("conveyor.out.stats.item_source");
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
        public AbstractContainer tailEnd() {
            Reader<String> reader = this.stream.readString("conveyor.out.stats.tail_end");
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
        public String[] linkedModulesName() {
            Reader<ArrayList> reader = this.stream.readArray("conveyor.out.link.modules");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return new String[0];
            ArrayList arrayList = optional.get();
            String[] array = new String[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                Object object = arrayList.get(i);
                JsonElement element = JsonParser.parseString(object.toString());
                if (!element.isJsonObject()) {
                    array[i] = "";
                    continue;
                }
                JsonObject jsonObject = element.getAsJsonObject();
                if (!jsonObject.has("module") || !jsonObject.get("module").isJsonPrimitive() ||
                        !jsonObject.getAsJsonPrimitive("module").isString()) {
                    array[i] = "";
                    continue;
                }
                array[i] = jsonObject.getAsJsonPrimitive("module").getAsString();
            }
            return array;
        }

        @Override
        public LinkedModule[] linkedModules() {
            Reader<ArrayList> reader = this.stream.readArray("conveyor.out.link.modules");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            LinkedModule[] array;
            if (!optional.isPresent())
                return new LinkedModule[0];
            ArrayList arrayList = optional.get();
            array = new LinkedModule[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                Object object = arrayList.get(i);
                JsonElement element = JsonParser.parseString(object.toString());
                if (!element.isJsonObject()) {
                    array[i] = null;
                    continue;
                }
                JsonObject jsonObject = element.getAsJsonObject();
                if (!jsonObject.has("module") || !jsonObject.has("face") || !jsonObject.has("position")) {
                    array[i] = null;
                    continue;
                }
                String moduleName = jsonObject.getAsJsonPrimitive("module").getAsString();
                int face = jsonObject.getAsJsonPrimitive("face").getAsInt();
                if (face < 0 || face >= LinkedModule.Face.values().length) {
                    array[i] = null;
                    continue;
                }
                int position = jsonObject.getAsJsonPrimitive("position").getAsInt();
                Optional<ModuleBase<?>> moduleBaseOptional = this.card.getModuleManager().getModule(moduleName);
                if (!moduleBaseOptional.isPresent()) {
                    array[i] = null;
                    continue;
                }
                array[i] = new LinkedModule(moduleBaseOptional.get(), new Point(LinkedModule.Face.values()[face], position));
            }
            return array;
        }

        @Override
        public int updateTicks() {
            Reader<Integer> reader = getStream().read("conveyor.out.stats.update_ticks", int.class);
            Optional<Integer> optional = reader.read();
            reader.close();
            return optional.orElse(20);
        }

        @Override
        protected void update() {
            this.accept = true;
        }

        @Override
        public boolean inject(Item<?> item) {
            if (!this.accept)
                return false;

            Reader<ArrayList> reader = this.stream.readArray("conveyor.out.view.content");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return false;
            ArrayList list = optional.get();
            Writer<ArrayList> writer = this.stream.write("conveyor.in.view.content", ArrayList.class);
            ConveyorItem<?> conveyorItem = ConveyorItem.fromItem(item);
            list.add(0, conveyorItem.getItem());

            int length = length();
            if (list.size() == length) {
                if (list.get(length - 1) != null) {
                    AbstractContainer abstractContainer = tailEnd();
                    if (abstractContainer == null) {
                        System.err.println("Could not get tail end container in conveyor inject method !");
                        return false;
                    }
                    abstractContainer.inject(new Item<>(Object.class, list.get(length - 1)));
                }
                List<Object> currentList = new ArrayList<>(list.size() - 1);
                for (int i = 0; i < list.size() - 1; i++)
                    currentList.add(list.get(i));
                list = new ArrayList<>(currentList);
            }

            writer.write(list);
            writer.push();
            writer.close();

            this.accept = false;

            LinkedModule linkedModule = getCaptor();
            if (linkedModule == null)
                return false;

            ModuleBase<?> moduleBase = linkedModule.getModuleBase();
            if (!(moduleBase instanceof Captor))
                return false;

            Captor captor = (Captor) moduleBase;
            return captor.capt();
        }

        @Override
        public boolean eject(Item<?> item) {
            Reader<ArrayList> reader = this.stream.readArray("conveyor.out.view.content");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return false;
            ArrayList content = optional.get();
            if (!content.contains(item.getItem()))
                return false;

            if (!(item instanceof ConveyorItem))
                return false;

            ConveyorItem<?> conveyorItem = (ConveyorItem<?>) item;
            content.set(conveyorItem.position, null);

            Writer<ArrayList> writer = this.stream.write("conveyor.in.view.content", ArrayList.class);
            if (!writer.write(content))
                return false;
            writer.push();
            writer.close();
            return true;
        }

        @Override
        public ModuleIOStream getStream() {
            if (this.stream != null)
                return this.stream;
            this.stream = new IOStream(this);
            return this.stream;
        }

        @Override
        public AbstractModuleFile file() {
            if (this.moduleFile != null)
                return this.moduleFile;
            this.moduleFile = new AbstractModuleFile(this) {
                @Override
                public @NotNull String mainFile() {
                    return "modules/conveyor/conveyor.json";
                }
            };
            return this.moduleFile;
        }

        @Override
        public LinkedModule[] links() {
            return linkedModules();
        }

        @Override
        public int[] size() {
            return new int[] {1, 1, 4, 3, 0, 0};
        }

        @Override
        public Point[] linkablePoint() {
            if (this.linkablePoint != null)
                return this.linkablePoint;

            Point[] points = new Point[] {
                    new Point(LinkedModule.Face.LEFT, 0),
                    new Point(LinkedModule.Face.RIGHT, 0),
                    new Point(LinkedModule.Face.FRONT, 10),
                    new Point(LinkedModule.Face.FRONT, 30),
                    new Point(LinkedModule.Face.FRONT, 40),
                    new Point(LinkedModule.Face.FRONT, 50),
                    new Point(LinkedModule.Face.BEHIND, 30),
                    new Point(LinkedModule.Face.BEHIND, 40),
                    new Point(LinkedModule.Face.BEHIND, 50),
            };

            return this.linkablePoint = points;
        }

        @Nullable
        @Override
        public LinkedModule getCaptor() {
            LinkedModule[] linkedModules = linkedModules();
            LinkedModule captor = null;
            for (LinkedModule linkedModule : linkedModules) {
                if (!(linkedModule.getModuleBase() instanceof Captor))
                    continue;
                captor = linkedModule;
                break;
            }
            return captor;
        }

        @Override
        public int captQueue() {
            LinkedModule captor = getCaptor();
            if (captor == null)
                return -1;
            Point point = captor.getPoint();
            return point.position();
        }

        @Override
        public ConveyorItem<?>[] read() {
            return rawContent();
        }
    }

    private static class ConveyorPropertyMap extends AbstractPropertyMap {

        private final Conveyor conveyor;

        private ConveyorPropertyMap(Conveyor conveyor) {
            this.conveyor = conveyor;
        }

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(this.conveyor, key);

            TypedPropertyEntries<T> entry = this.entries.filter(clazz);
            return entry.get(key).value();
        }
    }

    private static class ConveyorUpdater extends CardRunnable {

        private final Conveyor conveyor;

        private ConveyorUpdater(Conveyor conveyor) {
            this.conveyor = conveyor;
        }

        @Override
        protected void run() {
            String[] linkedModulesName = this.conveyor.linkedModulesName();
            ModuleManager manager = this.conveyor.card.getModuleManager();
            PropertyMap map = manager.getModuleMap();
            for (String moduleName : linkedModulesName) {
                if (map.containKey(moduleName))
                    continue;
                System.err.println(conveyor.getClass() + " need module '" + moduleName + "' !");
                System.err.println("Restart to try again !");
                cancel();
                return;
            }
            this.conveyor.update();
            AbstractContainer container = this.conveyor.itemSource();
            container.eject(null);
            LinkedModule[] linkedModules = this.conveyor.linkedModules();
            for (LinkedModule linkedModule : linkedModules) {
                if (linkedModule.getModuleBase() instanceof Updatable)
                    ((Updatable) linkedModule.getModuleBase()).update();
            }
        }
    }

    private static class IOStream implements ModuleIOStream {

        private final Conveyor conveyor;
        private final PropertyMap packetMap;

        private IOStream(Conveyor conveyor) {
            this.conveyor = conveyor;
            this.packetMap = new PacketParser(this.conveyor.file()).parse();
        }

        @Override
        public <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new NoWriter<>(key, type);

            PacketIn packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.conveyor.card.getModuleManager(), key,
                        new PacketSource(this.conveyor.getClass(), this.conveyor.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.INPUT.ordinal())
                    return new NoWriter<>(key, type);
                packet = (PacketIn) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new NoWriter<>(key, type);
            }
            return PacketDispatcher.inDispatch(this.conveyor.card, packet, type);
        }

        @Override
        public <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new EmptyReader<>(key, type);

            PacketOut packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.conveyor.card.getModuleManager(), key,
                        new PacketSource(this.conveyor.getClass(), this.conveyor.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.OUTPUT.ordinal())
                    return new EmptyReader<>(key, type);
                packet = (PacketOut) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new EmptyReader<>(key, type);
            }
            return PacketDispatcher.outDispatch(this.conveyor.card, packet, type);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.packetMap;
        }
    }

    public static class ConveyorItem<E> extends Item<E> {

        protected int position;

        public ConveyorItem(Class<E> type, E item) {
            super(type, item);
            this.position = 0;
        }

        protected static <U> ConveyorItem<U> fromItem(Item<U> item) {
            if (item instanceof ConveyorItem)
                return (ConveyorItem<U>) item;
            return new ConveyorItem<>(item.getType(), item.getItem());
        }

        public int getPosition() {
            return this.position;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            ConveyorItem<?> that = (ConveyorItem<?>) o;
            if (this.position != that.position)
                return false;

            return super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + position;
            return result;
        }

        @Override
        public String toString() {
            return "ConveyorItem{" +
                    "type=" + type +
                    ", item=" + item +
                    ", position=" + position +
                    '}';
        }
    }
}
