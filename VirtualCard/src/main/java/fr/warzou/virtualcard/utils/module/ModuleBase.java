package fr.warzou.virtualcard.utils.module;

import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.module.file.AbstractModuleFile;
import fr.warzou.virtualcard.utils.module.stream.ModuleStream;

/**
 * Module basic methods.
 * @author Warzou
 * @version 0.0.2
 * @param <T> target module stream {@link fr.warzou.virtualcard.utils.module.stream.ModuleInputStream},
 * {@link fr.warzou.virtualcard.utils.module.stream.ModuleOutputStream} or
 * {@link fr.warzou.virtualcard.utils.module.stream.ModuleIOStream}
 */
public interface ModuleBase<T extends ModuleStream> {

    /**
     * That return module name
     * @return module name
     */
    String moduleName();

    /**
     * This method return {@link ModuleStream} = {@code T}
     * <p>If only output, stream is {@link fr.warzou.virtualcard.utils.module.stream.ModuleOutputStream}</p>
     * <p>If only input, stream is {@link fr.warzou.virtualcard.utils.module.stream.ModuleInputStream}</p>
     * <p>If IO, stream is {@link fr.warzou.virtualcard.utils.module.stream.ModuleIOStream}</p>
     * @return module stream
     */
    T getStream();

    /**
     * That return main properties of this module (if is a auto generation this map contain only the module name)
     * @return module {@link PropertyMap}
     */
    PropertyMap getProperties();

    /**
     * Return {@link AbstractModuleFile} in link to this module
     * @return module file interpreter
     */
    AbstractModuleFile file();

}
