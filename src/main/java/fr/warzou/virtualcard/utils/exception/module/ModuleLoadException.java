package fr.warzou.virtualcard.utils.exception.module;

public class ModuleLoadException extends Exception {

    public ModuleLoadException(int index, FailPart failPart) {
        super("Couldn't load module with index '" + index + "' in modulelist.json.\n" +
                "Caused by : " + failPart.name());
    }

    public ModuleLoadException(String moduleName, Format format) {
        super("Couldn't load module '" + moduleName + "'\n" +
                "Caused by : " + format.name());
    }

    public enum FailPart {
        NAME_INFORMER_MISSING, MAIN_INFORMER_MISSING
    }

    public enum Format {
        MISSING_NAME, MISSING_STREAM_TYPE, NOT_JSON_OBJECT
    }

}
