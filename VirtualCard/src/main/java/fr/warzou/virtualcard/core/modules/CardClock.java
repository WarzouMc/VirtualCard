package fr.warzou.virtualcard.core.modules;

import fr.warzou.virtualcard.api.core.ticktask.CardRunnable;
import fr.warzou.virtualcard.api.core.ticktask.CardTask;
import fr.warzou.virtualcard.api.core.ticktask.CardTick;
import fr.warzou.virtualcard.api.environment.EnvironmentComponent;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractPropertyMap;
import fr.warzou.virtualcard.exception.packet.PacketNameException;
import fr.warzou.virtualcard.exception.packet.PacketNameInterpreterException;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.file.AbstractModuleFile;
import fr.warzou.virtualcard.utils.module.packets.PacketSource;
import fr.warzou.virtualcard.utils.module.packets.packet.PacketConstructor;
import fr.warzou.virtualcard.utils.module.packets.packet.PacketDispatcher;
import fr.warzou.virtualcard.utils.module.packets.packet.PacketOut;
import fr.warzou.virtualcard.utils.module.packets.packet.PacketParser;
import fr.warzou.virtualcard.utils.module.stream.ModuleOutputStream;
import fr.warzou.virtualcard.utils.module.stream.executor.impl.EmptyReader;
import fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;

public abstract class CardClock extends EnvironmentComponent<ModuleOutputStream> {

    private final PropertyMap propertyMap;
    private AbstractModuleFile moduleFile;
    private final ModuleOutputStream stream;
    private static CardClock system;

    public CardClock() {
        this.propertyMap = new ClockPropertyMap(this);
        this.propertyMap.put("now.ticks", nowTicks());
        this.propertyMap.put("now.seconds", nowSeconds());
        this.propertyMap.put("now.minutes", nowMinutes());
        this.propertyMap.put("now.hours", nowHours());
        this.propertyMap.put("instant", instant());
        this.propertyMap.put("name", "system.clock");
        this.stream = new OutputStream(this);
    }

    public static CardClock system() {
        if (system == null)
            system = new CardSystemClock();
        return system;
    }

    public static CardClock newSystem() {
        return new CardSystemClock();
    }

    public static void sleep(int ticks) {
        try {
            Thread.sleep(ticks * Math.round(1000.0 * CardTick.TICK));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract Instant instant();

    public abstract long nowTicks();

    public abstract long nowSeconds();

    public abstract long nowMinutes();

    public abstract long nowHours();

    @Override
    public String moduleName() {
        try {
            return (String) this.propertyMap.getProperty("name").value();
        } catch (MissingPropertyException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ModuleOutputStream getStream() {
        return this.stream;
    }

    @Override
    public AbstractModuleFile file() {
        if (this.moduleFile != null)
            return this.moduleFile;
        this.moduleFile = new AbstractModuleFile(this) {
            @Override
            public @NotNull String mainFile() {
                return "modules/clock/clock.json";
            }
        };
        return this.moduleFile;
    }

    @Override
    public PropertyMap getProperties() {
        return this.propertyMap;
    }

    private static class CardSystemClock extends CardClock {

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(nowTicks() * Math.round(1000.0 * CardTick.TICK));
        }

        @Override
        public long nowTicks() {
            return Math.round(System.currentTimeMillis() / (CardTick.TICK * 1000.0));
        }

        @Override
        public long nowSeconds() {
            return Math.round(nowTicks() * CardTick.TICK);
        }

        @Override
        public long nowMinutes() {
            long seconds = nowSeconds();
            return seconds / 60;
        }

        @Override
        public long nowHours() {
            long minutes = nowMinutes();
            return minutes / 60;
        }
    }

    private static class ClockPropertyMap extends AbstractPropertyMap {

        private final CardClock clock;

        private ClockPropertyMap(CardClock clock) {
            super();
            this.clock = clock;
        }

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(this.clock, key);

            TypedPropertyEntries<T> typedPropertyEntries = this.entries.filter(clazz);
            return typedPropertyEntries.get(key).value();
        }
    }

    private class OutputStream implements ModuleOutputStream {

        private final CardClock clock;
        private final PropertyMap map;
        private final PropertyMap packetMap;

        private OutputStream(@NotNull CardClock clock) {
            this.clock = clock;
            this.map = new ClockPropertyMap(this.clock);
            this.map.put("system.clock.out.instance", CardClock.class);
            this.map.put("system.clock.out.now.ticks", Long.class);
            this.map.put("system.clock.out.now.seconds", Long.class);
            this.map.put("system.clock.out.now.minutes", Long.class);
            this.map.put("system.clock.out.now.hours", Long.class);
            this.map.put("system.clock.out.now.instant", Instant.class);
            this.packetMap = new PacketParser(this.clock.file()).parse();
        }

        @Override
        public <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type) {
            if (key.isEmpty())
                return new EmptyReader<>(key, type);
            PacketOut packet;
            try {
                packet = (PacketOut) new PacketConstructor(card.getModuleManager(), key, new PacketSource(this.clock.getClass(),
                        nowTicks())).getPacket();
            } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
                e.printStackTrace();
                return new EmptyReader<>(key, type);
            }
            if (!this.map.containKey(key))
                return PacketDispatcher.outDispatch(this.clock.card, packet, type);

            Reader<String> reader = PacketDispatcher.outDispatch(this.clock.card, packet, String.class);
            Optional<String> optionalKey = reader.read();
            reader.close();
            if (!optionalKey.isPresent()) {
                try {
                    throw new NullPointerException("Failed to read packet '" + packet + "'.");
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return new EmptyReader<>(key, type);
                }
            }

            try {
                if (!type.isAssignableFrom((Class<?>) this.map.getProperty(key).value()))
                    throw new ClassCastException(type + " is not " + this.map.getProperty(key).value());
            } catch (MissingPropertyException e) {
                e.printStackTrace();
                return new EmptyReader<>(key, type);
            }

            key = optionalKey.get();
            String[] keySplit = key.split(":");
            Property<T> property;
            if (keySplit.length == 1) {
                key = keySplit[0];
                try {
                    property = this.clock.environment.getPropertyMap().getProperty(key, type);
                    return fromProperty(property, type);
                } catch (MissingPropertyException e) {
                    e.printStackTrace();
                    return new EmptyReader<>(key, type);
                }
            }
            key = keySplit[1];
            try {
                return fromProperty(this.clock.propertyMap.getProperty(key, type), type);
            } catch (MissingPropertyException e) {
                e.printStackTrace();
            }
            return new EmptyReader<>(key, type);
        }

        private <T> Reader<T> fromProperty(Property<T> property, Class<T> clazz) {
            return new Reader<T>() {
                private CardTask task;
                private boolean open = true;

                private Reader<T> close(int ticks) {
                    this.task = new CardRunnable() {
                        @Override
                        protected void run() {
                            if (!open)
                                return;
                            cancel();
                            close();
                        }
                    }.runTaskLater(card, ticks);
                    return this;
                }

                @Override
                public void close() {
                    this.open = false;
                    if (this.task != null && this.task.isActive())
                        this.task.cancel();
                }

                @Override
                public String key() {
                    return property.key();
                }

                @Override
                public Class<T> type() {
                    return clazz;
                }

                @Override
                public Optional<T> read() {
                    if (!this.open) {
                        try {
                            throw new IllegalStateException("This reader is close.");
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                            return Optional.empty();
                        }
                    }
                    return Optional.ofNullable(property.value());
                }
            }.close(60 * 20);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.packetMap;
        }
    }
}
