package fr.warzou.virtualcard.exception.property;

import fr.warzou.virtualcard.utils.module.ModuleBase;

public class MissingPropertyException extends Exception {

    public MissingPropertyException(String property) {
        super("Couldn't find '" + property + "' property !");
    }

    public MissingPropertyException(ModuleBase<?> moduleBase, String property) {
        super("Couldn't find '" + property + "' property in '" + moduleBase.moduleName() + "' environment !");
    }

    public MissingPropertyException(String property, Class<?> wrongClass) {
        super("'" + property + "' property is not a '" + wrongClass.getName() + "' !");
    }

}
