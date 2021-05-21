package fr.warzou.virtualcard.api.starter;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.api.core.plugin.CardPlugin;
import fr.warzou.virtualcard.utils.property.Property;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.SinglePropertyEntry;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class Launcher {

    private Card card;
    private final File current;

    Launcher() {
        System.out.println("Init environment file");
        Path path = Paths.get("").toAbsolutePath();
        System.out.println("Environment detected : '" + path + "'");
        this.current = path.toFile();
    }

    protected void launch() {
        this.card = new Card();
        this.card.getLogger().log("Launch...");
        this.card.getLogger().log("Post process start");
        postLaunchProcess();
        System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Please wait 2 seconds...");
        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ((AbstractCard) this.card).finishStartProcess();
        this.card.start();
        PropertyMap plugins = this.card.getPluginsManager().getPluginRegister().getPropertyMap();
        System.out.println("Start all plugins !");
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).toString() + "##################");
        for (SinglePropertyEntry<CardPlugin> entry : plugins.entries().filter(CardPlugin.class)) {
            Property<CardPlugin> plugin = entry.value();
            if (plugin.value() == null)
                continue;
            plugin.value().onStart();
        }
        justeInitPacket();
    }

    private void postLaunchProcess() {
        System.out.println("Load current environment files list !");
        String[] files = this.current.list();
        if (files == null)
            throw new NullPointerException("Could not found directory/file list in '" + this.current.getName() + "' directory !");

        System.out.println("Load logger files!");
        loggerFileLoader(files);
        moduleFileLoader(files);
        pluginsLoader(files);
    }

    private void loggerFileLoader(String[] files) {
        File logsFile = basicDirectoryCreator(files, "logs");
        System.out.println("Logger initialization !");
        this.card.getLogger().initLogFiles(logsFile, System.currentTimeMillis());
        System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Logger file load success");
    }

    private void moduleFileLoader(String[] files) {
        File moduleFile = basicDirectoryCreator(files, "modules");
        System.out.println("Modules file initialization");
        URL url = Main.class.getResource("Main.class");
        if (url == null) {
            System.err.println("Couldn't get 'Main.class' resource url !");
            return;
        }
        String protocol = url.getProtocol();
        if (!"jar".equals(protocol)) {
            System.err.println("This is not a jar file !");
            return;
        }

        try {
            System.out.println("Load jar resources !");
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                String name = entry.getName();
                if (!name.startsWith("modules/"))
                    continue;
                if (name.equals("modules/modulelist.json"))
                    continue;
                name = name.substring(8);
                System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Module file detection : " + name);
                String path = moduleFile.getPath() + File.separator + name;
                File file = new File(path);
                if (name.endsWith(".json")) {
                    file.createNewFile();
                    InputStream inputStream = jarFile.getInputStream(entry);
                    int length;
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((length = inputStream.read()) > 0)
                        fileOutputStream.write(length);
                    fileOutputStream.close();
                    continue;
                }
                file.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN) + "Module file load success");
    }

    private void pluginsLoader(String[] files) {
        File pluginFile = basicDirectoryCreator(files, "plugins");
        System.out.println("Start plugins loading !");

        CardPluginLoader pluginLoader = new CardPluginLoader(this.card, pluginFile);
        pluginLoader.loadPlugins();
        System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Plugin load finish");
    }

    private File basicDirectoryCreator(String[] files, String name) {
        System.out.println("Check for create file '" + name + "'");
        if (!Arrays.asList(files).contains(name)) {
            System.out.println("Create '" + name + "' directory");
            File pluginFile = new File(this.current.getPath() + File.separator + name);
            if (!pluginFile.mkdirs()) {
                new InternalError("Could not create '" + name + "' directory !").printStackTrace();
                if (this.card != null)
                    this.card.getLogger().close();
                fatalError();
                System.exit(0);
            }
        }
        File file = new File(this.current.getPath() + File.separator + name);
        System.out.println("Check file existence !");
        if (!file.exists()) {
            new InternalError("Could not found '" + name + "' directory !").printStackTrace();
            if (this.card != null)
                this.card.getLogger().close();
            fatalError();
            System.exit(0);
        }
        System.out.println("Success file '" + file.getName() + "'creation");
        return file;
    }

    private void justeInitPacket() {
        ModuleManager moduleManager = this.card.getModuleManager();
        PropertyMap moduleMap = moduleManager.getModuleMap();
        List<ModuleBase<?>> moduleBases = new ArrayList<>();
        moduleMap.entries().filter(ModuleBase.class).forEach(entry -> moduleBases.add(entry.value().value()));
        for (ModuleBase<?> module : moduleBases)
            module.getStream().getPacketMap();
    }

    private void fatalError() {
        System.out.println(Ansi.ansi().bg(Ansi.Color.RED).toString() + "This error is fatal, so the programme will be stop in 2 seconds !");
        try {
            Thread.sleep(2*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
