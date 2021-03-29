package fr.warzou.virtualcard.api.core.module;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.exception.module.ModuleLoadException;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ModuleLoader {

    public void load(@NotNull InputStream moduleListStream) {
        JsonElement jsonElement;
        jsonElement = JsonParser.parseReader(new InputStreamReader(moduleListStream));

        JsonObject mainObject = jsonElement.getAsJsonObject();
        JsonArray array = mainObject.getAsJsonArray("modules");

        List<JsonModule> jsonModules = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            try {
                jsonModules.add(JsonModule.asJsonModule(elementModule(element, i)));
            } catch (ModuleLoadException e) {
                e.printStackTrace();
            }
        }


    }

    protected JsonElement elementModule(@NotNull JsonElement element, int index) throws ModuleLoadException {
        JsonObject object = element.getAsJsonObject();
        if (!object.has("name"))
            throw new ModuleLoadException(index, ModuleLoadException.FailPart.NAME_INFORMER_MISSING);
        if (!object.has("main"))
            throw new ModuleLoadException(index, ModuleLoadException.FailPart.MAIN_INFORMER_MISSING);

        String name = object.get("name").getAsString();
        String main = object.get("main").getAsString();
        String sourcePath = "module/" + name + "/" + main + ".json";

        InputStream inputStream = Card.class.getClassLoader().getResourceAsStream(sourcePath);
        if (inputStream == null)
            throw new NullPointerException("Couldn't get '" + sourcePath + "' resource !");

        return JsonParser.parseReader(new InputStreamReader(inputStream));
    }

    private static class ModuleBuilder {

    }

}
