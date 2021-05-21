package fr.warzou.virtualcard.exception.module;

import fr.warzou.virtualcard.utils.module.file.AbstractModuleFile;

public class MalformedModuleException extends Exception {

    public MalformedModuleException(AbstractModuleFile moduleFile, String message) {
        super(moduleFile.getModuleBase().moduleName() + " couldn't be read correctly.\n" +
                message);
    }
}
