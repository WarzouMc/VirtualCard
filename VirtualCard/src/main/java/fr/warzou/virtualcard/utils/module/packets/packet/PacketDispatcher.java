package fr.warzou.virtualcard.utils.module.packets.packet;

import com.google.gson.*;
import com.google.gson.internal.Primitives;
import com.google.gson.stream.JsonWriter;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.ticktask.CardRunnable;
import fr.warzou.virtualcard.api.core.ticktask.CardTask;
import fr.warzou.virtualcard.api.events.events.in.PacketInEvent;
import fr.warzou.virtualcard.api.events.events.out.PacketOutEvent;
import fr.warzou.virtualcard.exception.module.MalformedModuleException;
import fr.warzou.virtualcard.utils.event.EventsManager;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.file.AbstractModuleFile;
import fr.warzou.virtualcard.utils.module.packets.PacketDestination;
import fr.warzou.virtualcard.utils.module.packets.PacketPath;
import fr.warzou.virtualcard.utils.module.packets.PacketTrace;
import fr.warzou.virtualcard.utils.module.stream.ModuleInputStream;
import fr.warzou.virtualcard.utils.module.stream.ModuleOutputStream;
import fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader;
import fr.warzou.virtualcard.utils.module.stream.executor.writer.Writer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

/**
 * Utils class to dispatch packets.
 * @author Warzou
 * @version 0.0.2
 */
public abstract class PacketDispatcher {

    /**
     * Just to have same {@link Gson} everytime
     */
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    /**
     * Dispatch input packet without target class type.
     * <p>That just do :</p>
     * {@code return inDispatch(card, packet, Object.class);}
     * @param card card
     * @param packet pack to dispatch
     * @return inDispatch with {@link Object#getClass()} in class {@link #inDispatch(Card, PacketIn, Class)}}
     */
    public static Writer<Object> inDispatch(@NotNull Card card, @NotNull PacketIn packet) {
        return inDispatch(card, packet, Object.class);
    }

    /**
     * Dispatch an input packet.
     * @param card card
     * @param packet packet to dispatch
     * @param clazz class of value
     * @param <T> {@link Writer} type
     * @return packet dispatch result
     */
    public static <T> Writer<T> inDispatch(@NotNull Card card, @NotNull PacketIn packet, @NotNull Class<T> clazz) {
        PacketTrace packetTrace = packet.trace();
        PacketDestination destination = packetTrace.getDestination();
        ModuleBase<? extends ModuleInputStream> moduleBase = (ModuleBase<? extends ModuleInputStream>) destination.getModuleBase();

        EventsManager eventsManager = card.getEventManager();
        eventsManager.callEvent(new PacketInEvent(packet));
        return new Writer<T>() {
            private CardTask task;
            private boolean open = true;

            @Override
            public String key() {
                return packet.packetName();
            }

            @Override
            public Class<T> type() {
                return clazz;
            }

            @Override
            public boolean write(T value) {
                if (!this.open) {
                    try {
                        throw new IOException("This writer is closed.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                PacketPath path = packet.packetPath();
                JsonElement element;
                try {
                    if (path.asFile().isEmpty())
                        element = moduleBase.file().readFilePart(path.getPath()).parse();
                    else {
                        String file = path.asFile();
                        element = moduleBase.file().readFilePart("").parse(file);
                    }
                } catch (MalformedModuleException e) {
                    e.printStackTrace();
                    return false;
                }

                if (element == null || !element.isJsonObject())
                    return false;

                JsonObject node = element.getAsJsonObject();
                String endpoint = packet.endpoint();
                if (!node.has(endpoint))
                    return false;

                Class<T> clazz = Primitives.wrap(type());
                JsonElement nodeEndValue;
                if (Collection.class.isAssignableFrom(clazz)) {
                    JsonArray array = new JsonArray();
                    Collection<?> collection = (Collection<?>) value;
                    collection.forEach(o -> {
                        JsonElement current;
                        if (o instanceof JsonElement)
                            current = (JsonElement) o;
                        else current = parser(o, Object.class);
                        array.add(current);
                    });
                    nodeEndValue = array;
                } else {
                    nodeEndValue = parser(value, clazz);
                }
                node.add(endpoint, nodeEndValue);
                return true;
            }

            @Override
            public void push() {
                if (!this.open) {
                    try {
                        throw new IOException("This writer is closed.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                PacketPath packetPath = packet.packetPath();
                String file = packetPath.asFile();

                AbstractModuleFile abstractModuleFile = moduleBase.file();
                String mainFileString = abstractModuleFile.mainFile();

                if (file.isEmpty()) {
                    try {
                        writeInFile(mainFileString, true);
                    } catch (URISyntaxException | IOException | MalformedModuleException e) {
                        e.printStackTrace();
                        return;
                    }
                    return;
                }
                try {
                    writeInFile(file, false);
                } catch (URISyntaxException | IOException | MalformedModuleException e) {
                    e.printStackTrace();
                }
            }

            private void writeInFile(String file, boolean main) throws URISyntaxException, IOException, MalformedModuleException {
                File target = new File(file);

                FileWriter writer = new FileWriter(target);
                JsonElement pushElement = main ? moduleBase.file().mainJsonElement() : moduleBase.file().readFilePart("")
                        .parse(file);
                JsonWriter jsonWriter = new JsonWriter(writer);
                jsonWriter.setIndent("\t");
                jsonWriter.setLenient(true);
                GSON.toJson(pushElement, jsonWriter);
                writer.close();
            }

            private Writer<T> close(int ticks) {
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
        }.close(20 * 60);
    }

    /**
     * Dispatch input packet without target class type.
     * <p>That just do :</p>
     * {@code return outDispatch(card, packet, Object.class);}
     * @param card card
     * @param packet target packet to dispatch
     * @return outDispatch with {@link Object#getClass()} in class {@link #outDispatch(Card, PacketOut, Class)}}
     */
    public static Reader<?> outDispatch(@NotNull Card card, @NotNull PacketOut packet) {
        return outDispatch(card, packet, Object.class);
    }

    /**
     * Dispatch an output packet.
     * @param card card
     * @param packet packet to dispatch
     * @param clazz class of value
     * @param <T> {@link Reader} type
     * @return packet dispatch result
     */
    public static <T> Reader<T> outDispatch(@NotNull Card card, @NotNull PacketOut packet, @NotNull Class<T> clazz) {
        PacketTrace trace = packet.trace();
        PacketDestination destination = trace.getDestination();
        ModuleBase<? extends ModuleOutputStream> moduleBase = (ModuleBase<? extends ModuleOutputStream>) destination.getModuleBase();
        EventsManager eventsManager = card.getEventManager();
        eventsManager.callEvent(new PacketOutEvent(packet));
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
                if (task != null && this.task.isActive())
                    this.task.cancel();
            }

            @Override
            public String key() {
                return packet.packetName();
            }

            @Override
            public Class<T> type() {
                return clazz;
            }

            @Override
            public Optional<T> read() {
                if (!this.open)
                    try {
                        throw new IOException("This reader is closed.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }

                JsonElement element;
                try {
                    if (packet.packetPath().asFile().isEmpty())
                        element = moduleBase.file().readFilePart(packet.packetPath().getPath() + "//" +
                                packet.endpoint()).parse();
                    else {
                        String file = packet.packetPath().asFile();
                        element = moduleBase.file().readFilePart(packet.endpoint()).parse(file);
                    }
                } catch (MalformedModuleException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
                if (element == null || element.isJsonNull())
                    return Optional.empty();

                //Case T is a instance of Number
                Class<T> wrap = Primitives.wrap(clazz);
                Object object = GSON.fromJson(element, Object.class);
                if (Number.class.isAssignableFrom(wrap) && object instanceof Number)
                    return Optional.ofNullable(parser(GSON.fromJson(element, Number.class), wrap));

                if (!wrap.isAssignableFrom(object.getClass())) {
                    try {
                        throw new ClassCastException("Could not cast '" + key() + "' key to " + clazz);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }
                }
                return Optional.of(GSON.fromJson(element, wrap));
            }
        }.close(20 * 60);
    }

    /**
     * Parse a {@link Number} into a class who extend {@link Number}.
     * @param number value
     * @param clazz cast
     * @param <N> class cast type
     * @return number cast to clazz
     */
    protected static <N> N parser(Number number, Class<N> clazz) {
        clazz = Primitives.wrap(clazz);
        if (!Number.class.isAssignableFrom(clazz)) {
            try {
                throw new ClassCastException(clazz + " is not castable to " + Number.class);
            } catch (ClassCastException e) {
                e.printStackTrace();
                return null;
            }
        }

        String value = number.toString();
        Number finalNumber = null;
        try {
            if (clazz == Integer.class)
                finalNumber = Integer.valueOf(value);
            if (clazz == Float.class)
                finalNumber = Float.valueOf(value);
            if (clazz == Long.class)
                finalNumber = Long.valueOf(value);
            if (clazz == Double.class)
                finalNumber = Double.valueOf(value);
            if (clazz == Byte.class)
                finalNumber = Byte.valueOf(value);
            if (clazz == Short.class)
                finalNumber = Short.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return clazz.cast(finalNumber);
    }

    /**
     * Parse a object into a {@link JsonElement}
     * @param t value to parse
     * @param wrap wrapped class type
     * @param <T> t class type
     * @return a {@link JsonElement} from t param
     */
    protected static <T> JsonElement parser(T t, Class<T> wrap) {
        JsonElement element;
        if (Number.class.isAssignableFrom(wrap)) {
            element = new JsonPrimitive((Number) t);
        } else if (String.class.isAssignableFrom(wrap)) {
            element = new JsonPrimitive((String) t);
        } else if (Boolean.class.isAssignableFrom(wrap)) {
            element = new JsonPrimitive((Boolean) t);
        } else if (Character.class.isAssignableFrom(wrap)) {
            element = new JsonPrimitive((Character) t);
        } else if (Collection.class.isAssignableFrom(wrap)) {
            return new JsonArray(((Collection<?>) t).size());
        } else if (JsonElement.class.isAssignableFrom(wrap)) {
            element = wrap.asSubclass(JsonElement.class).cast(t);
        } else {
            element = GSON.toJsonTree(t, wrap);
        }
        return element;
    }
}
