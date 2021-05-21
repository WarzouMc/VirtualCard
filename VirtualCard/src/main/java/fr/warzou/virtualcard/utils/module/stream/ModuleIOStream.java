package fr.warzou.virtualcard.utils.module.stream;

/**
 * Just allows to create IO stream.
 * <p>Only the methods {@link ModuleIOStream#write(String)} and {@link ModuleIOStream#read(String)} need to be implemented cause the others methods call them</p>
 * @author Warzou
 * @version 0.0.2
 */
public interface ModuleIOStream extends ModuleInputStream, ModuleOutputStream {
}
