package fr.warzou.virtualcard.core.modules.captor;

import fr.warzou.virtualcard.api.environment.EnvironmentComponent;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractPropertyMap;
import fr.warzou.virtualcard.api.events.events.captor.CaptorCaptEvent;
import fr.warzou.virtualcard.core.modules.CardClock;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.container.conveyor.Conveyor;
import fr.warzou.virtualcard.core.modules.link.UniqueCaptorLinkable;
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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public abstract class Captor extends EnvironmentComponent<ModuleIOStream> {

    protected final PropertyMap propertyMap;
    protected AbstractModuleFile moduleFile;
    protected ModuleIOStream stream;

    public Captor() {
        this.propertyMap = new CaptorPropertyMap(this);
        this.propertyMap.put("name", "captor");
    }

    public static Captor systemCaptor() {
        return new SystemCaptor();
    }

    @Nullable
    public abstract UniqueCaptorLinkable link();

    public ComparatorType comparator() {
        return ComparatorType.asComparator(checkType());
    }

    public abstract Object[] constants();

    /**
     * Only {@link Number} comparator, (min/max/equal)
     * @return Value of the packet captor.out.stat.check_type
     */
    @Nullable
    public abstract String checkType();

    @Nullable
    public abstract Item<?> getCapt();

    public abstract boolean capt();

    public abstract int captInterpretation();

    @Nullable
    public abstract CaptResult result();

    public Class<?> type() {
        //todo no implemented, later shrug
        return Number.class;
    }

    @NotNull
    @Override
    public String moduleName() {
        try {
            return this.propertyMap.getProperty("name", String.class).value();
        } catch (MissingPropertyException e) {
            e.printStackTrace();
        }
        return "captor";
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
                return "modules/captor/captor.json";
            }
        };
        return this.moduleFile;
    }

    private static class SystemCaptor extends Captor {

        @Override
        public UniqueCaptorLinkable link() {
            Reader<String> reader = this.stream.readString("captor.out.stat.link");
            Optional<String> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return null;

            String moduleName = optional.get();
            Optional<ModuleBase<?>> optionalModuleBase = this.card.getModuleManager().getModule(moduleName);
            if (!optionalModuleBase.isPresent())
                return null;

            ModuleBase<?> moduleBase = optionalModuleBase.get();
            if (!(moduleBase instanceof UniqueCaptorLinkable))
                return null;
            return (UniqueCaptorLinkable) moduleBase;
        }

        @Nullable
        @Override
        public String checkType() {
            Reader<String> reader = this.stream.readString("captor.out.stat.check_type");
            Optional<String> optional = reader.read();
            reader.close();
            return optional.orElse(null);
        }

        @Override
        public Object[] constants() {
            Reader<ArrayList> reader = this.stream.readArray("captor.out.stat.check_constants");
            Optional<ArrayList> optional = reader.read();
            reader.close();
            if (!optional.isPresent())
                return new Object[0];

            ArrayList arrayList = optional.get();
            Object[] constants = new Object[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                Object object = arrayList.get(i);
                constants[i] = object;
            }
            return constants;
        }

        @Nullable
        @Override
        public Item<?> getCapt() {
            Reader<?> reader = this.stream.read("captor.out.capt.now");
            Optional<?> optional = reader.read();
            reader.close();
            return optional.map(o -> new Item<>(Object.class, o)).orElse(new Item<>(Object.class, null));
        }

        @Override
        public boolean capt() {
            UniqueCaptorLinkable captorLinkable = link();
            if (captorLinkable == null)
                return false;

            int listen = captorLinkable.captQueue();
            Conveyor.ConveyorItem<?>[] conveyorItems = captorLinkable.read();

            Optional<Conveyor.ConveyorItem<?>> optional = Arrays.stream(conveyorItems)
                    .filter(conveyorItem -> conveyorItem != null && conveyorItem.getPosition() == listen).findFirst();
            if (!optional.isPresent())
                return false;

            Conveyor.ConveyorItem<?> item = optional.get();
            Writer<Object> writer = stream.write("captor.in.capt.now", Object.class);
            if (!writer.write(item.getItem())) {
                writer.close();
                return false;
            }
            writer.push();
            writer.close();
            EventsManager eventsManager = this.card.getEventManager();
            eventsManager.callEvent(new CaptorCaptEvent(this, CardClock.system().nowTicks(), getCapt(), result()));
            return true;
        }

        @Override
        public int captInterpretation() {
            Item<?> item = getCapt();
            if (item == null)
                return -1;

            if (item.getItem() == null || !item.getType().isAssignableFrom(Number.class))
                return -1;

            Object[] constants = constants();
            if (constants.length == 0)
                return 0;

            Number[] numbers = new Number[constants.length];

            for (int i = 0; i < constants.length; i++) {
                Object constant = constants[i];
                if (constant instanceof Number) {
                    numbers[i] = (Number) constant;
                    continue;
                }
                new IllegalArgumentException(constant.getClass() + " is not currently supported, please insert only Numbers !")
                        .printStackTrace();
                return -1;
            }

            ComparatorType comparatorType = comparator();
            Number number = (Number) item.getItem();
            for (int i = 0; i < numbers.length; i++) {
                Number compare = numbers[i];
                if (comparatorType == ComparatorType.EQUAL) {
                    if (number.equals(compare))
                        return i;
                    continue;
                }
                if (comparatorType == ComparatorType.MAX) {
                    if (Math.max(number.doubleValue(), compare.doubleValue()) == number.doubleValue())
                        return i;
                    continue;
                }
                if (comparatorType == ComparatorType.MIN) {
                    if (Math.min(number.doubleValue(), compare.doubleValue()) == number.doubleValue())
                        return i;
                    continue;
                }
            }
            return numbers.length;
        }

        @Override
        public @Nullable CaptResult result() {
            Item<?> item = getCapt();
            if (item == null)
                return null;
            int value = captInterpretation();
            return new CaptResult(item, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;

            if (!(obj instanceof SystemCaptor))
                return false;

            SystemCaptor target = (SystemCaptor) obj;
            return target.moduleName().equals(this.moduleName());
        }
    }

    private static class CaptorPropertyMap extends AbstractPropertyMap {

        private final Captor captor;

        private CaptorPropertyMap(Captor captor) {
            this.captor = captor;
        }

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(this.captor, key);

            TypedPropertyEntries<T> entry = this.entries.filter(clazz);
            return entry.get(key).value();
        }
    }

    private static class IOStream implements ModuleIOStream {

        private final Captor captor;
        private final PropertyMap packetMap;

        private IOStream(Captor captor) {
            this.captor = captor;
            this.packetMap = new PacketParser(this.captor.file()).parse();
        }

        @Override
        public <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new NoWriter<>(key, type);
            PacketIn packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.captor.card.getModuleManager(), key,
                        new PacketSource(this.captor.getClass(), this.captor.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.INPUT.ordinal())
                    return new NoWriter<>(key, type);
                packet = (PacketIn) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new NoWriter<>(key, type);
            }
            return PacketDispatcher.inDispatch(this.captor.card, packet, type);
        }

        @Override
        public <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new EmptyReader<>(key, type);
            PacketOut packet;
            try {
                PacketConstructor constructor = new PacketConstructor(this.captor.card.getModuleManager(), key,
                        new PacketSource(this.captor.getClass(), this.captor.environment.systemClock().nowTicks()));
                Packet undetermined = constructor.getPacket();
                if (undetermined.packetType() != ModuleStreamType.OUTPUT.ordinal())
                    return new EmptyReader<>(key, type);
                packet = (PacketOut) undetermined;
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new EmptyReader<>(key, type);
            }
            return PacketDispatcher.outDispatch(this.captor.card, packet, type);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.packetMap;
        }
    }

    public enum ComparatorType {

        MIN("min", Number.class),
        MAX("max", Number.class),
        EQUAL("equal", Number.class);

        private final String comparator;
        private final Type target;

        ComparatorType(String comparator, Type target) {
            this.comparator = comparator;
            this.target = target;
        }

        private static ComparatorType asComparator(String value) {
            if (value == null || value.isEmpty())
                return EQUAL;
            return Arrays.stream(values())
                    .filter(comparatorType -> comparatorType.comparator.equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(EQUAL);
        }

        public String getComparator() {
            return this.comparator;
        }

        public Type getTarget() {
            return this.target;
        }
    }

    public static class CaptResult {

        private final Item<?> item;
        private final int value;

        public CaptResult(Item<?> item, int value) {
            this.item = item;
            this.value = value;
        }

        public Item<?> getItem() {
            return this.item;
        }

        public int getValue() {
            return this.value;
        }
    }
}
