package fr.warzou.virtualcard.utils.plugin;

/**
 * This interface group different information could contain the plugin jar file.
 * @author Warzou
 * @version 0.0.2
 */
public interface PluginInformation {

    /**
     * That return plugin name specified in the plugin.json
     * @return plugin name
     */
    String name();

    /**
     * That return plugin authors specified in the plugin.json
     * @return plugin authors
     */
    String[] authors();

    /**
     * That return plugin version specified in the plugin.json
     * @return plugin version
     */
    String version();

    /**
     * That return plugin main class path specified in the plugin.json
     * @return plugin main class path
     */
    String main();

    /**
     * That return plugin jar file name
     * @return plugin jar name
     */
    String rawName();
}
