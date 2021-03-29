package fr.warzou.virtualcard.api.core.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.warzou.virtualcard.utils.exception.module.ModuleLoadException;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.stream.ModuleIOStream;
import fr.warzou.virtualcard.utils.module.stream.ModuleInputStream;
import fr.warzou.virtualcard.utils.module.stream.ModuleOutputStream;
import fr.warzou.virtualcard.utils.module.stream.ModuleStream;
import org.jetbrains.annotations.NotNull;

abstract class JsonModule<T extends ModuleStream> {

    protected static JsonModule<?> asJsonModule(@NotNull JsonElement element) throws ModuleLoadException {
        if (!element.isJsonObject())
            throw new ModuleLoadException("Fail before load name sry", ModuleLoadException.Format.NOT_JSON_OBJECT);

        JsonObject object = element.getAsJsonObject();
        if (!object.has("name"))
            throw new ModuleLoadException("Fail before load name sry", ModuleLoadException.Format.MISSING_NAME);

        String name = object.get("name").getAsString();
        if (!object.has("stream_type"))
            throw new ModuleLoadException(name, ModuleLoadException.Format.MISSING_STREAM_TYPE);

        int streamType = object.get("stream_type").getAsInt();
        JsonModule<?> jsonModule;

        if (streamType == 0)
            jsonModule = new InputJsonModule(element, name);
        else if (streamType == 1)
            jsonModule = new OutputJsonModule(element, name);
        else
            jsonModule = new InputOutputJsonModule(element, name);

        return jsonModule;
    }

    private final JsonElement element;
    private final String name;
    private final int streamType;

    private JsonModule(JsonElement element, String name, int streamType) {
        this.element = element;
        this.name = name;
        this.streamType = streamType;
    }

    protected abstract ModuleBase<T> asModule();

    private static class InputJsonModule extends JsonModule<ModuleInputStream> {

        private InputJsonModule(JsonElement element, String name) {
            super(element, name, 0);
        }

        @Override
        protected ModuleBase<ModuleInputStream> asModule() {
            return null;
        }
    }

    private static class OutputJsonModule extends JsonModule<ModuleOutputStream> {

        private OutputJsonModule(JsonElement element, String name) {
            super(element, name, 1);
        }

        @Override
        protected ModuleBase<ModuleOutputStream> asModule() {
            return null;
        }
    }

    private static class InputOutputJsonModule extends JsonModule<ModuleIOStream> {

        private InputOutputJsonModule(JsonElement element, String name) {
            super(element, name, 2);
        }

        @Override
        protected ModuleBase<ModuleIOStream> asModule() {
            return null;
        }
    }
}
