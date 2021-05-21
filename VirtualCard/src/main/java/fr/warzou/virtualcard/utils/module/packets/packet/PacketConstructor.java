package fr.warzou.virtualcard.utils.module.packets.packet;

import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.exception.packet.PacketNameException;
import fr.warzou.virtualcard.exception.packet.PacketNameInterpreterException;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.packets.PacketDestination;
import fr.warzou.virtualcard.utils.module.packets.PacketPath;
import fr.warzou.virtualcard.utils.module.packets.PacketSource;
import fr.warzou.virtualcard.utils.module.packets.PacketTrace;
import fr.warzou.virtualcard.utils.module.stream.ModuleStreamType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Allow to construct a packet from its name.
 * @author Warzou
 * @version 0.0.2
 */
public class PacketConstructor {

    /**
     * Current module manager.
     */
    private final ModuleManager moduleManager;
    /**
     * Target packet name.
     */
    private final String packetName;
    /**
     * Constructed packet.
     */
    private final Packet packet;

    /**
     * Construct a new instance of {@link PacketConstructor}
     * @param moduleManager module manager
     * @param packetName packet name
     * @param source source of packet
     * @throws PacketNameException when name is malformed.
     * @throws PacketNameInterpreterException when name is couldn't be interpreted.
     * @throws MissingPropertyException normally never throws.
     */
    public PacketConstructor(@NotNull ModuleManager moduleManager, @NotNull String packetName, PacketSource source)
            throws PacketNameException, PacketNameInterpreterException, MissingPropertyException {
        this.moduleManager = moduleManager;
        this.packetName = packetName;
        String[] nameSplit = nameSplitter();
        ModuleBase<?> moduleBase = exactModule(nameSplit[0]);
        ModuleStreamType moduleStreamType = exactStreamType(nameSplit[1]);
        String property = nameSplit[2];
        PacketPath packetPath = Objects.requireNonNull(moduleBase.getStream().getPacketMap()).getProperty(packetName, PacketPath.class).value();

        this.packet = moduleStreamType == ModuleStreamType.INPUT ?
                in(new PacketTrace(source, new PacketDestination(moduleBase)), property, packetPath)
                : out(new PacketTrace(source, new PacketDestination(moduleBase)), property, packetPath);
    }

    /**
     * That returns packet who be generate.
     * @return generated packet
     */
    public Packet getPacket() {
        return this.packet;
    }

    /**
     * Split packet name
     * @return all part of packet name
     * @throws PacketNameException when name is malformed.
     */
    private String[] nameSplitter() throws PacketNameException {
        String module = moduleNameExtractor(this.packetName);
        if (module == null)
            throw new PacketNameException("Could not find a module in '" + this.packetName + "' packet");

        String stream = streamExtractor(module);
        if (stream == null)
            throw new PacketNameException("Could not find a stream type in '" + this.packetName + "' packet (stream is [in/out])");

        String property = propertyExtractor(module, stream);
        if (property == null)
            throw new PacketNameException("Could not find property in '" + this.packetName + "' packet");
        return new String[] {module, stream, property};
    }

    /**
     * Return packet module
     * @param moduleName module name
     * @return detected {@link ModuleBase}
     * @throws PacketNameInterpreterException when name is couldn't be interpreted.
     * @throws MissingPropertyException normally never throws.
     */
    @NotNull
    private ModuleBase<?> exactModule(@NotNull String moduleName) throws PacketNameInterpreterException, MissingPropertyException {
        PropertyMap map = this.moduleManager.getModuleMap();
        for (String key : map.keys()) {
            if (key.equals(moduleName))
                return map.getProperty(key, ModuleBase.class).value();
        }
        throw new PacketNameInterpreterException("'" + moduleName + "' module interpretation fail in '" + this.packetName + "' packet");
    }

    /**
     * Returns {@link ModuleStreamType}.
     * @param stream stream string value
     * @return stream type
     * @throws PacketNameInterpreterException when name is couldn't be interpreted.
     */
    @NotNull
    private ModuleStreamType exactStreamType(@NotNull String stream) throws PacketNameInterpreterException {
        Optional<ModuleStreamType> optional = ModuleStreamType.fromString(stream);
        if (optional.isPresent())
            return optional.get();
        throw new PacketNameInterpreterException("'" + stream + "' stream interpretation fail in '" + this.packetName + "' packet");
    }

    /**
     * Extract module name from a packet.
     * @param packetName from packet
     * @return module name
     */
    private String moduleNameExtractor(String packetName) {
        String[] split = packetName.split("\\.");
        int nameBound = 0;
        for (String s : split) {
            if (s.equals("in") || s.equals("out"))
                break;
            nameBound++;
        }
        StringBuilder predictedNameBuilder = new StringBuilder();
        for (int i = 0; i < nameBound; i++)
            predictedNameBuilder.append(split[i]).append(".");

        String predictedName = predictedNameBuilder.toString();
        predictedName = predictedName.substring(0, predictedName.length() - 1);
        PropertyMap map = this.moduleManager.getModuleMap();
        for(String key : map.keys()) {
            if (predictedName.equals(key))
                return key;
        }
        return null;
    }

    /**
     * {@link ModuleStreamType} string value
     * @param moduleName module name
     * @return stream type
     */
    private String streamExtractor(String moduleName) {
        String nameSub = this.packetName.substring(moduleName.length() + 1);
        String[] split = nameSub.split("\\.");
        if (split.length == 0)
            return null;
        if (!split[0].equals("in") && !split[0].equals("out"))
            return null;
        return split[0];
    }

    /**
     * Return target property.
     * @param moduleName module name
     * @param stream stream string value
     * @return packet target property
     */
    private String propertyExtractor(String moduleName, String stream) {
        String sub = this.packetName.substring((moduleName + "." + stream + ".").length());
        return sub.length() == 0 ? null : sub;
    }

    /**
     * Returns input packet
     * @param packetTrace trace
     * @param property target property
     * @param packetPath packet path
     * @return constructed packet
     */
    private PacketIn in(PacketTrace packetTrace, String property, PacketPath packetPath) {
        return new PacketIn() {
            @Override
            public PacketPath packetPath() {
                return packetPath;
            }

            @Override
            public String endpoint() {
                return packetPath.getEnd();
            }

            @Override
            public String packetName() {
                return packetName;
            }

            @Override
            public String property() {
                return property;
            }

            @Override
            public PacketTrace trace() {
                return packetTrace;
            }

            @Override
            public String toString() {
                return "PacketIn{" +
                        "name='" + packetName() + '\'' +
                        ", trace=" + trace() +
                        '}';
            }
        };
    }

    /**
     * Returns output packet
     * @param packetTrace trace
     * @param property target property
     * @param packetPath packet path
     * @return constructed packet
     */
    private PacketOut out(PacketTrace packetTrace, String property, PacketPath packetPath) {
        return new PacketOut() {
            @Override
            public PacketPath packetPath() {
                return packetPath;
            }

            @Override
            public String endpoint() {
                return packetPath.getEnd();
            }

            @Override
            public String packetName() {
                return packetName;
            }

            @Override
            public String property() {
                return property;
            }

            @Override
            public PacketTrace trace() {
                return packetTrace;
            }

            @Override
            public String toString() {
                return "PacketOut{" +
                        "name='" + packetName() + '\'' +
                        ", trace=" + trace() +
                        '}';
            }
        };
    }
}
