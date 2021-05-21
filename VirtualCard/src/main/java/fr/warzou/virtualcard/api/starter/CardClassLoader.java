package fr.warzou.virtualcard.api.starter;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.plugin.CardPlugin;
import fr.warzou.virtualcard.utils.plugin.PluginInformation;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.JarFile;

public class CardClassLoader extends URLClassLoader {

    private final JarFile jar;
    private final CardPlugin cardPlugin;

    public CardClassLoader(Card card, File file, ClassLoader parent, String name, String version, List<String> authors,
                           String main) throws IOException {
        super(new URL[] {file.toURI().toURL()}, parent);
        System.out.println("Main class loader for plugin '" + name + "'");
        this.jar = new JarFile(file);

        Class<?> clazz;
        try {
            clazz = Class.forName(main, true, this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }

        Class<? extends CardPlugin> pluginClass = clazz.asSubclass(CardPlugin.class);
        String[] authorsArray = authors.toArray(new String[0]);
        System.out.println("Init plugin '" + name + "'");
        this.cardPlugin = CardPlugin.init(card, pluginClass, new PluginInformation() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public String[] authors() {
                return authorsArray;
            }

            @Override
            public String version() {
                return version;
            }

            @Override
            public String main() {
                return main;
            }

            @Override
            public String rawName() {
                return file.getName();
            }
        });
        if (this.cardPlugin != null)
            System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Plugin '" + name + "' is now loaded !");
    }

    protected CardPlugin getPlugin() {
        return this.cardPlugin;
    }
}
