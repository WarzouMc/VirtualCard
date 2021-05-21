package fr.warzou.virtualcard.utils.module.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.exception.module.MalformedModuleException;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import org.jetbrains.annotations.Nullable;

/**
 * Read module raw file.
 * <p>'//' mean a new indent level.</p>
 * <p>Example : </p>
 * <pre>
 *     {
 *         'a': {
 *             'b': {
 *                 'd': 5
 *             },
 *             'c': {
 *                 'e': 6
 *             }
 *         }
 *     }
 * </pre>
 *
 * <p>Path to read 'd' : {@code "a//b//d"}</p>
 * <p>Path to read 'e' : {@code "a//c//e"}</p>
 * @author Warzou
 * @version 0.0.2
 */
public class ModuleInnerReader {

    /**
     * Source {@link AbstractModuleFile}
     */
    private final AbstractModuleFile moduleFile;
    /**
     * File part
     */
    private String part;

    /**
     * Create a new instance of {@link ModuleInnerReader}.
     * @param moduleFile target {@link AbstractModuleFile}
     * @param part file part
     */
    public ModuleInnerReader(AbstractModuleFile moduleFile, String part) {
        this.moduleFile = moduleFile;
        this.part = part;
    }

    /**
     * Read a new sub part of the this file.
     * @param nextPart new file part
     * @return this
     */
    public ModuleInnerReader read(String nextPart) {
        this.part += "//" + nextPart;
        return this;
    }

    /**
     * Read parent part of this file.
     * @return this
     */
    public ModuleInnerReader back() {
        String[] split = this.part.split("//");
        if (split.length <= 1)
            return this;
        String last = split[split.length - 1];
        int lastLenght = 2 + last.length();
        this.part = this.part.substring(0, this.part.length() - lastLenght);
        return this;
    }

    /**
     * Parse this into {@link JsonElement}
     * @return target element
     * @throws MalformedModuleException when path is invalid, element is not a {@link JsonObject} or a part is not a {@link JsonObject}.
     */
    @Nullable
    public JsonElement parse() throws MalformedModuleException {
        JsonElement element = this.moduleFile.mainJsonElement();
        return globalParse(element);
    }

    /**
     * Read into a other module file.
     * @param children children file path
     * @return target element
     * @throws MalformedModuleException when path is invalid, element is not a {@link JsonObject} or a part is not a {@link JsonObject}.
     */
    @Nullable
    public JsonElement parse(String children) throws MalformedModuleException {
        PropertyMap map = this.moduleFile.getChildren();
        if (!map.containKey(children))
            return null;

        Property<JsonElement> property;
        try {
            property = map.getProperty(children, JsonElement.class);
        } catch (MissingPropertyException e) {
            e.printStackTrace();
            return null;
        }

        JsonElement element = property.value();
        return globalParse(element);
    }

    /**
     * Read file part in a target {@link JsonElement}
     * @param element main
     * @return target element
     * @throws MalformedModuleException when path is invalid, element is not a {@link JsonObject} or a part is not a {@link JsonObject}.
     */
    @Nullable
    public JsonElement globalParse(JsonElement element) throws MalformedModuleException {
        if (element == null)
            return null;

        if (!element.isJsonObject())
            throw new MalformedModuleException(this.moduleFile, "Need a JsonObject");

        if (this.part.isEmpty())
            return element;

        JsonObject object = element.getAsJsonObject();
        String[] splitPart = this.part.split("//");
        for (int i = 0; i < splitPart.length; i++) {
            String split = splitPart[i];
            if (!object.has(split))
                throw new MalformedModuleException(this.moduleFile, "Could not find '" + split + "' key.");
            element = object.get(split);
            if (i == splitPart.length - 1)
                break;
            if (i != splitPart.length - 1 && !element.isJsonObject())
                throw new MalformedModuleException(this.moduleFile, "'" + split + "' must be a JsonObject");
            object = element.getAsJsonObject();
        }
        return element;
    }
}