package fr.warzou.virtualcard.utils.module;

import fr.warzou.virtualcard.utils.module.stream.ModuleStream;

public interface ModuleBase<T extends ModuleStream> {

    String moduleName();

    T getStream();

}
