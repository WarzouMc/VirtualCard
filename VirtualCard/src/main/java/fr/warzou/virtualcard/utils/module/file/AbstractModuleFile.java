package fr.warzou.virtualcard.utils.module.file;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.TypedPropertyEntries;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractLockablePropertyMap;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.stream.ModuleStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * Module file interpreter.
 * @author Warzou
 * @version 0.0.2
 */
public abstract class AbstractModuleFile {

    /**
     * Module main {@link JsonElement}
     */
    protected JsonElement jsonElement;
    /**
     * Current {@link ModuleBase}
     */
    private final ModuleBase<? extends ModuleStream> moduleBase;
    /**
     * Children module {@link JsonElement}
     */
    private final PropertyMap children;

    /**
     * Create a new instance of {@link AbstractModuleFile}.
     * @param moduleBase target {@link ModuleBase}
     */
    protected AbstractModuleFile(ModuleBase<? extends ModuleStream> moduleBase) {
        this.moduleBase = moduleBase;
        this.children = new ImplPropertyMap();
        loadChildren();
        ((ImplPropertyMap) this.children).lock();
    }

    /**
     * Load all other file of moduleBase
     */
    private void loadChildren() {
        new ModuleWalker(this, this.children).walk();
    }

    /**
     * Returns {@link ModuleBase} main json file path
     * @return module main file
     */
    @NotNull
    public abstract String mainFile();

    /**
     * Module file content into a {@link JsonElement}
     * @return module into a json element
     */
    @Nullable
    public JsonElement mainJsonElement() {
        if (this.jsonElement != null)
            return this.jsonElement;

        java.io.InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(mainFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream == null) {
            try {
                throw new NullPointerException("Could not find '" + mainFile() + "' file.");
            } catch (NullPointerException e) {
                e.printStackTrace();
                return null;
            }
        }
        return this.jsonElement = JsonParser.parseReader(new InputStreamReader(inputStream));
    }

    /**
     * Return new instance of {@link ModuleInnerReader}
     * @param part file part
     * @return reader of file part
     */
    public ModuleInnerReader readFilePart(String part) {
        return new ModuleInnerReader(this, part);
    }

    /**
     * Returns moduleBase field.
     * @return module base
     */
    public ModuleBase<? extends ModuleStream> getModuleBase() {
        return this.moduleBase;
    }

    /**
     * Return every modules file with their content.
     * @return every module files
     */
    public PropertyMap getChildren() {
        return this.children;
    }

    /**
     * Juste an {@link AbstractLockablePropertyMap}.
     * @author Warzou
     * @version 0.0.2
     */
    private static class ImplPropertyMap extends AbstractLockablePropertyMap {

        @Override
        public <T> Property<T> getProperty(@NotNull String key, Class<T> clazz) throws MissingPropertyException {
            if (!containKey(key))
                throw new MissingPropertyException(key);

            TypedPropertyEntries<T> typedPropertyEntries = this.entries.filter(clazz);
            return typedPropertyEntries.get(key).value();
        }
    }
}
