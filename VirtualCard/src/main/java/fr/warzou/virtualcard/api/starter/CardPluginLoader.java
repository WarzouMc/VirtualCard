package fr.warzou.virtualcard.api.starter;

import com.google.gson.*;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.plugin.CardPlugin;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;
import fr.warzou.virtualcard.utils.command.commandsystem.CommandRegister;
import fr.warzou.virtualcard.exception.command.CommandFormatException;
import org.fusesource.jansi.Ansi;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class CardPluginLoader {

    private final Card card;
    private final File file;

    CardPluginLoader(Card card, File file) {
        this.card = card;
        this.file = file;
    }

    protected void loadPlugins() {
        File[] files = this.file.listFiles();
        if (files == null)
            return;

        for (File pluginFile : files) {
            if (pluginFile.isDirectory() || !pluginFile.getName().endsWith(".jar"))
                continue;
            System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Jar detection : " + pluginFile.getName());
            CardPlugin cardPlugin;
            try {
                cardPlugin = getPlugin(pluginFile);
                if (cardPlugin == null)
                    System.err.println("Could not load '" + pluginFile.getName() + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.card.getPluginsManager().finish();
    }

    private CardPlugin getPlugin(File pluginFile) throws IOException {
        System.out.println(Ansi.ansi().bg(Ansi.Color.YELLOW).toString() + "Try to load plugin jar '" + pluginFile.getName() + "'.");
        JarFile jarFile = new JarFile(pluginFile);
        System.out.println("Try to find 'plugin.json' file in the jar file.");
        JarEntry pluginEntry = jarFile.getJarEntry("plugin.json");
        if (pluginEntry == null) {
            try {
                throw new FileNotFoundException("Couldn't found 'plugin.json' file !");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        System.out.println("Load plugin information");
        JsonElement informationElement;
        try {
            informationElement = JsonParser.parseReader(new InputStreamReader(jarFile.getInputStream(pluginEntry)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (!informationElement.isJsonObject()) {
            System.err.println("Fail cause 'plugin.json' content doesn't represent json object !");
            return null;
        }

        JsonObject informationObject = informationElement.getAsJsonObject();
        if (!informationObject.has("name") || !informationObject.get("name").isJsonPrimitive()) {
            System.err.println("Fail cause 'plugin.json' does not contain section 'name' or this section is not a primitive !");
            return null;
        }
        if (!informationObject.has("version") || !informationObject.get("version").isJsonPrimitive()) {
            System.err.println("Fail cause 'plugin.json' does not contain section 'version' or this section is not a primitive !");
            return null;
        }
        if (!informationObject.has("authors") || !informationObject.get("authors").isJsonArray()) {
            System.err.println("Fail cause 'plugin.json' does not contain section 'authors' or this section is not a string array !");
            return null;
        }
        if (!informationObject.has("main") || !informationObject.get("main").isJsonPrimitive()) {
            System.err.println("Fail cause 'plugin.json' does not contain section 'main' or this section is not a primitive !");
            return null;
        }

        JsonPrimitive namePrimitive = informationObject.getAsJsonPrimitive("name");
        JsonPrimitive versionPrimitive = informationObject.getAsJsonPrimitive("version");
        JsonArray authorsArray = informationObject.getAsJsonArray("authors");
        JsonPrimitive mainPrimitive = informationObject.getAsJsonPrimitive("main");

        if (!namePrimitive.isString() || !versionPrimitive.isString() || !mainPrimitive.isString()) {
            System.err.println("'name', 'version' and 'main' section need to be strings");
            return null;
        }

        System.out.println("Load name");
        String name = namePrimitive.getAsString().replace(" ", "_");
        System.out.println("Load version");
        String version = versionPrimitive.getAsString();
        System.out.println("Load authors");
        List<String> authors = new ArrayList<>();
        authorsArray.forEach(element -> {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())
                return;
            authors.add(element.getAsJsonPrimitive().getAsString());
        });
        System.out.println("Load main class path");
        String main = mainPrimitive.getAsString();
        try {
            pluginModuleLoader(jarFile, informationObject);
            pluginCommandLoader(name, informationObject);
            CardClassLoader cardClassLoader = new CardClassLoader(this.card, pluginFile, getClass().getClassLoader(),
                    name, version, authors, main);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN) + "Loaded : plugin name : " + name + ", version : " + version
                    + ", authors : " + authors + ", main : " + main + " !");
            return cardClassLoader.getPlugin();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void pluginModuleLoader(JarFile jarFile, JsonObject jsonObject) {
        if (!jsonObject.has("modules") || !jsonObject.get("modules").isJsonObject())
            return;

        System.out.println("Load modules...");
        JsonObject modulesObject = jsonObject.get("modules").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = modulesObject.entrySet();

        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String name = entry.getKey();
            System.out.println("Check module '" + name + "'");
            if (!entry.getValue().isJsonPrimitive() || !entry.getValue().getAsJsonPrimitive().isString()) {
                System.err.println("Couldn't load module '" + name + "' from plugin '" + jarFile.getName() + "' " +
                        "cause '" + entry.getValue().toString() + "' is not a String !");
                continue;
            }

            try {
                String path = entry.getValue().getAsJsonPrimitive().getAsString();
                String modulePath = Paths.get("").toAbsolutePath().toFile().getPath() + File.separator + "modules";
                Enumeration<JarEntry> enumeration = jarFile.entries();
                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = enumeration.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (!jarEntryName.startsWith(path))
                        continue;
                    jarEntryName = jarEntryName.substring(8);
                    File file = new File(modulePath + File.separator + jarEntryName);
                    if (!file.getName().endsWith(".json")) {
                        file.mkdirs();
                        continue;
                    }
                    file.createNewFile();
                    InputStream stream = jarFile.getInputStream(jarEntry);
                    int length;
                    FileOutputStream outputStream = new FileOutputStream(file);
                    while ((length = stream.read()) > 0)
                        outputStream.write(length);
                    outputStream.close();
                }
                System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Success module load");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pluginCommandLoader(String pluginName, JsonObject jsonObject) {
        if (!jsonObject.has("commands") || !jsonObject.get("commands").isJsonObject())
            return;

        System.out.println("Load commands...");
        JsonObject commandsObject = jsonObject.get("commands").getAsJsonObject();
        CommandRegister commandRegister = this.card.getCommandRegister();
        Set<Map.Entry<String, JsonElement>> entrySet = commandsObject.entrySet();

        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String name = entry.getKey();
            System.out.println("Check for command '" + name + "'");
            JsonElement value = entry.getValue();
            if (!value.isJsonObject()) {
                new CommandFormatException(pluginName, name, "Not a json object").printStackTrace();
                continue;
            }

            JsonObject commandObject = value.getAsJsonObject();
            if (!commandObject.has("description") || !commandObject.get("description").isJsonPrimitive() ||
                    !commandObject.get("description").getAsJsonPrimitive().isString()) {
                new CommandFormatException(pluginName, name, "Need a 'description' section, or 'description section " +
                        "isn't a string").printStackTrace();
                continue;
            }

            String description = commandObject.get("description").getAsJsonPrimitive().getAsString();
            String help = null;
            String[] alias = new String[0];

            if (commandObject.has("help") && commandObject.get("help").isJsonPrimitive()
                    && commandObject.get("help").getAsJsonPrimitive().isString()) {
                help = commandObject.get("help").getAsJsonPrimitive().getAsString();
            }

            if (commandObject.has("alias") && commandObject.get("alias").isJsonArray()) {
                JsonArray array = commandObject.get("alias").getAsJsonArray();
                List<String> aliasList = new ArrayList<>();
                for (JsonElement element : array) {
                    if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())
                        continue;
                    aliasList.add(element.getAsJsonPrimitive().getAsString());
                }
                alias = aliasList.toArray(new String[] {});
            }

            String[] finalAlias = alias;
            String finalHelp = help;
            Command command = new Command() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getDescription() {
                    return description;
                }

                @Override
                public String getHelpMessage() {
                    return finalHelp;
                }

                @Override
                public String[] getAlias() {
                    return finalAlias;
                }

                @Override
                public String source() {
                    return pluginName;
                }
            };
            System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Success command load");
            commandRegister.addCommand(command);
        }
    }
}
