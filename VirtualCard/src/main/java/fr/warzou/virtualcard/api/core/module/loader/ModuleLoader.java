package fr.warzou.virtualcard.api.core.module.loader;

import com.google.gson.*;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.exception.module.ModuleLoadException;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleLoader {

    private final List<ModuleBase<?>> moduleBaseList;

    public ModuleLoader() {
        this.moduleBaseList = new ArrayList<>();
    }

    public ModuleManager load(@NotNull Card card, @NotNull InputStream moduleListStream) {
        JsonElement jsonElement;
        jsonElement = JsonParser.parseReader(new InputStreamReader(moduleListStream));

        JsonObject mainObject = jsonElement.getAsJsonObject();
        JsonArray array = mainObject.getAsJsonArray("modules");

        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            try {
                JsonModuleObject jsonModuleObject = elementModule(element, i);
                if (jsonModuleObject == null)
                    throw new ModuleLoadException("Could not load module with index " + i + ".");
                if (!jsonModuleObject.isAutoRegistry())
                    continue;
                moduleBaseList.add(JsonModule.asJsonModule(card, jsonModuleObject));
            } catch (ModuleLoadException e) {
                e.printStackTrace();
            }
        }
        return new ModuleManager(this);
    }

    public List<ModuleBase<?>> getModules() {
        return this.moduleBaseList;
    }

    protected JsonModuleObject elementModule(@NotNull JsonElement element, int index) throws ModuleLoadException {
        JsonObject object = element.getAsJsonObject();
        if (!object.has("name"))
            throw new ModuleLoadException(index, ModuleLoadException.FailPart.NAME_INFORMER_MISSING);
        if (!object.has("main"))
            throw new ModuleLoadException(index, ModuleLoadException.FailPart.MAIN_INFORMER_MISSING);

        String name = object.get("name").getAsString();
        String main = object.get("main").getAsString();
        String mainPath = "modules" + File.separator + name + File.separator + main + ".json";
        boolean autoRegistry = true;

        if (object.has("auto_registry") && object.get("auto_registry").isJsonPrimitive()) {
            JsonPrimitive primitive = object.getAsJsonPrimitive("auto_registry");
            if (primitive.isBoolean())
                autoRegistry = primitive.getAsBoolean();
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(mainPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (inputStream == null) {
            try {
                throw new NullPointerException();
            } catch (NullPointerException e) {
                return null;
            }
        }

        return new JsonModuleObject(name, mainPath, autoRegistry, JsonParser.parseReader(new InputStreamReader(inputStream)));
    }

    protected static class JsonModuleObject {

        private final String name;
        private final String path;
        private final boolean autoRegistry;
        private final JsonElement element;

        private JsonModuleObject(@NotNull String name, @NotNull String path, boolean autoRegistry, @NotNull JsonElement element) {
            this.name = name;
            this.path = path;
            this.autoRegistry = autoRegistry;
            this.element = element;
        }

        @NotNull
        public String getName() {
            return this.name;
        }

        @NotNull
        public String getPath() {
            return this.path;
        }

        public boolean isAutoRegistry() {
            return this.autoRegistry;
        }

        @NotNull
        public JsonElement getElement() {
            return this.element;
        }
    }
}
