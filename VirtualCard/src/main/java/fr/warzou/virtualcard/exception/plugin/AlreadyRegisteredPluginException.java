package fr.warzou.virtualcard.exception.plugin;

public class AlreadyRegisteredPluginException extends Exception {

    public AlreadyRegisteredPluginException(String rawName) {
        super("The plugin '" + rawName + "' is already registered !");
    }

}
