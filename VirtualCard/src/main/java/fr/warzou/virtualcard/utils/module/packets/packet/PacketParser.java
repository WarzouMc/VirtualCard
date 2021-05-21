package fr.warzou.virtualcard.utils.module.packets.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractLockablePropertyMap;
import fr.warzou.virtualcard.exception.module.MalformedModuleException;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.file.AbstractModuleFile;
import fr.warzou.virtualcard.utils.module.file.ModuleInnerReader;
import fr.warzou.virtualcard.utils.module.packets.PacketPath;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/**
 * Checkout every packet in a module file.
 * @author Warzou
 * @version 0.0.2
 */
public class PacketParser {

    /**
     * Module file of target module
     */
    private final AbstractModuleFile moduleFile;

    /**
     * Create a new instance of {@link PacketParser}.
     * @param moduleFile target {@link AbstractModuleFile}
     */
    public PacketParser(AbstractModuleFile moduleFile) {
        this.moduleFile = moduleFile;
    }

    /**
     * Return {@link PropertyMap} of packet en {@link PacketPath}.
     * Note this {@link PropertyMap} is a {@link AbstractLockablePropertyMap} and {@link AbstractLockablePropertyMap#lock()} is call.
     * So this map is immutable.
     * @return propertymap who contain in key a packet name and in value (only) {@link PacketPath} of target key
     */
    public PropertyMap parse() {
        LockablePropertyMap propertyMap = new LockablePropertyMap();
        ModuleInnerReader moduleReader = this.moduleFile.readFilePart("packets");

        moduleReader.read("out");
        try {
            JsonElement out = moduleReader.parse();
            add("", propertyMap, this.moduleFile.getModuleBase().moduleName() + ".out", "packets//out", 0, out);
        } catch (MalformedModuleException ignore) {}

        moduleReader.back().read("in");
        try {
            JsonElement in = moduleReader.parse();
            add("", propertyMap, this.moduleFile.getModuleBase().moduleName() + ".in", "packets//in", 0, in);
        } catch (MalformedModuleException ignore) {}

        propertyMap.lock();
        return propertyMap;
    }

    /**
     * Add all detected packets in a {@link PropertyMap}.
     * This walk in every sub section of {@link JsonObject} is element is {@link JsonObject}
     * @param file target file
     * @param map {@link PropertyMap} where put result
     * @param suffix in/out
     * @param path current path
     * @param treeLevel current level (s1.s2.s3 has a treeLevel of 3)
     * @param element current {@link JsonElement}
     * @return true if add is a success and false else
     */
    private boolean add(String file, PropertyMap map, String suffix, String path, int treeLevel, JsonElement element) {
        if (element == null || !element.isJsonObject())
            return false;
        JsonObject object = element.getAsJsonObject();
        if (treeLevel > 0) {
            if (!object.has("section_name"))
                return false;
            JsonElement sectionElement = object.get("section_name");
            if (!sectionElement.isJsonPrimitive())
                return false;
            JsonPrimitive jsonPrimitive = sectionElement.getAsJsonPrimitive();
            if (!jsonPrimitive.isString())
                return false;
            String sectionName = jsonPrimitive.getAsString();
            suffix = suffix + "." + sectionName;
        }
        Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            if (key.equals("section_name"))
                continue;
            JsonElement value = entry.getValue();
            if (value == null)
                continue;
            boolean addSuccess = add(file, map, suffix, path + "//" + key, treeLevel + 1, value);
            if (addSuccess)
                continue;
            if (this.moduleFile.getChildren().containKey(key.substring(2))) {
                if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString())
                    continue;
                String newSection = value.getAsJsonPrimitive().getAsString();
                try {
                    JsonElement newElement = this.moduleFile.getChildren().getProperty(key.substring(2), JsonElement.class)
                            .value();
                    add(key.substring(2), map, suffix, newSection, treeLevel + 1, newElement);
                } catch (MissingPropertyException e) {
                    e.printStackTrace();
                }
                continue;
            }
            map.put(suffix + "." + key, new PacketPath(file, path, key));
        }
        return true;
    }

    /**
     * Juste a implementation of {@link AbstractLockablePropertyMap}.
     * @author Warzou
     * @version 0.0.2
     */
    private static class LockablePropertyMap extends AbstractLockablePropertyMap {

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(key);

            TypedPropertyEntries<T> typedPropertyEntries = this.entries.filter(clazz);
            return typedPropertyEntries.get(key).value();
        }
    }
}
