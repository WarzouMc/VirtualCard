package fr.warzou.virtualcard.api.core.module.loader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.api.environment.CardEnvironment;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractLockablePropertyMap;
import fr.warzou.virtualcard.exception.module.ModuleLoadException;
import fr.warzou.virtualcard.exception.packet.PacketNameException;
import fr.warzou.virtualcard.exception.packet.PacketNameInterpreterException;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.file.AbstractModuleFile;
import fr.warzou.virtualcard.utils.module.packets.PacketSource;
import fr.warzou.virtualcard.utils.module.packets.packet.*;
import fr.warzou.virtualcard.utils.module.stream.*;
import fr.warzou.virtualcard.utils.module.stream.executor.impl.EmptyReader;
import fr.warzou.virtualcard.utils.module.stream.executor.impl.NoWriter;
import fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader;
import fr.warzou.virtualcard.utils.module.stream.executor.writer.Writer;
import org.jetbrains.annotations.NotNull;

abstract class JsonModule {

    protected static ModuleBase<?> asJsonModule(@NotNull Card card, @NotNull ModuleLoader.JsonModuleObject jsonModuleObject)
            throws ModuleLoadException {
        JsonElement element = jsonModuleObject.getElement();
        if (!element.isJsonObject())
            throw new ModuleLoadException("Fail before load name sry", ModuleLoadException.Format.NOT_JSON_OBJECT);

        JsonObject object = element.getAsJsonObject();
        if (!object.has("module_name"))
            throw new ModuleLoadException("Fail before load name sry", ModuleLoadException.Format.MISSING_NAME);

        String name = object.get("module_name").getAsString();
        if (!object.has("stream_type"))
            throw new ModuleLoadException(name, ModuleLoadException.Format.MISSING_STREAM_TYPE);

        int streamType = object.get("stream_type").getAsInt();
        ModuleBase<?> module;

        if (streamType == 0)
            module = new InputModule(card, element, jsonModuleObject.getPath(), name);
        else if (streamType == 1)
            module = new OutputModule(card, element, jsonModuleObject.getPath(), name);
        else
            module = new InputOutputModule(card, element, jsonModuleObject.getPath(), name);

        return module;
    }

    protected static <U> Reader<U> readPrototype(@NotNull String key, @NotNull Class<U> clazz,
                                                 @NotNull AbstractModuleBase<? extends ModuleOutputStream> moduleBase) {
        if (key.isEmpty())
            return new EmptyReader<>(key, clazz);

        PropertyMap packetMap = moduleBase.packetMap;
        if (!packetMap.containKey(key))
            return new EmptyReader<>(key, clazz);

        try {
            ModuleManager moduleManager = moduleBase.card.getModuleManager();
            CardEnvironment environment = moduleBase.card.getEnvironment();
            PacketConstructor packetConstructor = new PacketConstructor(moduleManager, key,
                    new PacketSource(moduleBase.getClass(), environment.systemClock().nowTicks()));
            Packet packet = packetConstructor.getPacket();
            if (packet.packetType() != ModuleStreamType.OUTPUT.ordinal())
                return new EmptyReader<>(key, clazz);
            return PacketDispatcher.outDispatch(moduleBase.card, (PacketOut) packet, clazz);
        } catch (MissingPropertyException | PacketNameInterpreterException | PacketNameException e) {
            e.printStackTrace();
        }
        return new EmptyReader<>(key, clazz);
    }

    private static <U> Writer<U> writePrototype(@NotNull String key, @NotNull Class<U> clazz,
                                                @NotNull AbstractModuleBase<? extends ModuleInputStream> moduleBase) {
        if (key.isEmpty())
            return new NoWriter<>(key, clazz);

        PropertyMap packetMap = moduleBase.packetMap;
        if (!packetMap.containKey(key))
            return new NoWriter<>(key, clazz);

        try {
            ModuleManager moduleManager = moduleBase.card.getModuleManager();
            CardEnvironment environment = moduleBase.card.getEnvironment();
            PacketConstructor packetConstructor = new PacketConstructor(moduleManager, key,
                    new PacketSource(moduleBase.getClass(), environment.systemClock().nowTicks()));
            Packet packet = packetConstructor.getPacket();
            if (packet.packetType() != ModuleStreamType.INPUT.ordinal())
                return new NoWriter<>(key, clazz);
            return PacketDispatcher.inDispatch(moduleBase.card, (PacketIn) packet, clazz);
        } catch (MissingPropertyException | PacketNameInterpreterException | PacketNameException e) {
            e.printStackTrace();
        }
        return new NoWriter<>(key, clazz);
    }

    private static abstract class AbstractModuleBase<T extends ModuleStream> implements ModuleBase<T> {

        protected final Card card;
        protected final JsonElement element;
        protected final String path;
        protected final String name;
        protected final PropertyMap propertyMap;
        private final ModuleFile moduleFile;

        protected final PropertyMap packetMap;

        protected AbstractModuleBase(Card card, JsonElement element, String path, String name) {
            this.card = card;
            this.element = element;
            this.path = path;
            this.name = name;
            this.propertyMap = new LockablePropertyMap(this);
            fillMap();
            ((LockablePropertyMap) this.propertyMap).lock();
            this.moduleFile = new ModuleFile(this, path, name);
            this.packetMap = new PacketParser(this.moduleFile).parse();
        }

        protected void fillMap() {
            this.propertyMap.put("name", this.name);
        }

        @Override
        public String moduleName() {
            return this.name;
        }

        @Override
        public AbstractModuleFile file() {
            return this.moduleFile;
        }
    }

    private static class InputModule extends AbstractModuleBase<ModuleInputStream> {

        private ModuleInputStream stream;

        private InputModule(Card card, JsonElement element, String path, String name) {
            super(card, element, path, name);
        }

        @Override
        protected void fillMap() {
            super.fillMap();
        }

        @Override
        public ModuleInputStream getStream() {
            if (this.stream != null)
                return this.stream;
            return this.stream = new InputStream(this);
        }

        @Override
        public PropertyMap getProperties() {
            return this.propertyMap;
        }
    }

    private static class OutputModule extends AbstractModuleBase<ModuleOutputStream> {

        private ModuleOutputStream stream;

        protected OutputModule(Card card, JsonElement element, String path, String name) {
            super(card, element, path, name);
        }

        @Override
        protected void fillMap() {
            super.fillMap();
        }

        @Override
        public ModuleOutputStream getStream() {
            if (this.stream != null)
                return this.stream;
            return this.stream = new OutputStream(this);
        }

        @Override
        public PropertyMap getProperties() {
            return this.propertyMap;
        }
    }

    private static class InputOutputModule extends AbstractModuleBase<ModuleIOStream> {

        private ModuleIOStream stream;

        private InputOutputModule(Card card, JsonElement element, String path, String name) {
            super(card, element, path, name);
        }

        @Override
        protected void fillMap() {
            super.fillMap();
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
    }

    private static class InputStream implements ModuleInputStream {

        private final AbstractModuleBase<ModuleInputStream> moduleBase;

        private InputStream(AbstractModuleBase<ModuleInputStream> moduleBase) {
            this.moduleBase = moduleBase;
        }

        @Override
        public <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type) {
            return writePrototype(key, type, this.moduleBase);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.moduleBase.packetMap;
        }
    }

    private static class OutputStream implements ModuleOutputStream {

        private final AbstractModuleBase<ModuleOutputStream> moduleBase;

        private OutputStream(AbstractModuleBase<ModuleOutputStream> moduleBase) {
            this.moduleBase = moduleBase;
        }

        @Override
        public <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type) {
            return readPrototype(key, type, this.moduleBase);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.moduleBase.packetMap;
        }
    }

    private static class IOStream implements ModuleIOStream {

        private final AbstractModuleBase<ModuleIOStream> moduleBase;

        private IOStream(AbstractModuleBase<ModuleIOStream> moduleBase) {
            this.moduleBase = moduleBase;
        }

        @Override
        public <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type) {
            return writePrototype(key, type, this.moduleBase);
        }

        @Override
        public <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type) {
            return readPrototype(key, type, this.moduleBase);
        }

        @Override
        public PropertyMap getPacketMap() {
            return this.moduleBase.packetMap;
        }
    }

    private static class LockablePropertyMap extends AbstractLockablePropertyMap {

        private final ModuleBase<? extends ModuleStream> moduleBase;

        private LockablePropertyMap(ModuleBase<? extends ModuleStream> moduleBase) {
            super();
            this.moduleBase = moduleBase;
        }

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(this.moduleBase, key);

            TypedPropertyEntries<T> typedPropertyEntries = this.entries.filter(clazz);
            return typedPropertyEntries.get(key).value();
        }
    }

    private static class ModuleFile extends AbstractModuleFile {

        private final String path;
        private final String main;

        private ModuleFile(ModuleBase<? extends ModuleStream> moduleBase, String path, String main) {
            super(moduleBase);
            this.path = path;
            this.main = main;
        }

        @Override
        public @NotNull String mainFile() {
            return this.path + "/" + this.main + ".json";
        }
    }
}
