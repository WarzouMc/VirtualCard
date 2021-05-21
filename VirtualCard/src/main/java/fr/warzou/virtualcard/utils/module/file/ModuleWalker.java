package fr.warzou.virtualcard.utils.module.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.warzou.virtualcard.utils.property.PropertyMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Walk in main module file to find every other files of this module.
 * @author Warzou
 * @version 0.0.2
 */
class ModuleWalker {

    /**
     * Target {@link JsonElement}.
     */
    private final JsonElement element;
    /**
     * {@link PropertyMap} where results will be placed.
     */
    private final PropertyMap map;

    /**
     * Create a new instance of {@link ModuleWalker}.
     * @param moduleFile main file
     * @param map result map
     */
    ModuleWalker(AbstractModuleFile moduleFile, PropertyMap map) {
        this.element = moduleFile.mainJsonElement();
        this.map = map;
    }

    /**
     * Create a new instance of {@link ModuleWalker}.
     * @param file target file
     * @param map result map
     */
    private ModuleWalker(String file, PropertyMap map) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (stream == null)
            throw new NullPointerException();

        this.element = JsonParser.parseReader(new InputStreamReader(stream));
        this.map = map;
    }

    /**
     * Walk in file
     */
    protected void walk() {
        if (this.element == null || !this.element.isJsonObject())
            return;

        JsonObject object = this.element.getAsJsonObject();
        object.entrySet().forEach(entry -> walk(entry.getValue()));
    }

    /**
     * Walk in {@link JsonElement} content.
     * @param element target element
     */
    private void walk(JsonElement element) {
        if (element == null || !element.isJsonObject())
            return;

        JsonObject object = element.getAsJsonObject();
        object.entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonElement current = entry.getValue();
            if (!key.startsWith("./") && !key.endsWith(".json")) {
                walk(current);
                return;
            }
            String realPath = key.substring(2);
            InputStream stream = null;
            try {
                stream = new FileInputStream(realPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (stream == null)
                return;
            this.map.put(realPath, JsonParser.parseReader(new InputStreamReader(stream)));
            new ModuleWalker(realPath, this.map).walk();
        });
    }
}
