package fr.warzou.virtualcard.utils.module.stream;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Stream type enumeration
 * @author Warzou
 * @version 0.0.2
 */
public enum ModuleStreamType {

    /**
     * Input stream
     */
    INPUT,
    /**
     * IO stream
     */
    INPUT_OUTPUT,
    /**
     * Output stream
     */
    OUTPUT;

    /**
     * This method allows to parse a {@link String} into a {@link ModuleStreamType}
     * @param string String at parse
     * @return empty optional is could not find associate stream, and optional with {@link ModuleStreamType} else
     */
    public static Optional<ModuleStreamType> fromString(@NotNull String string) {
        string = string.toLowerCase();
        if (string.equals("input") || string.equals("in"))
            return Optional.of(INPUT);
        if (string.equals("output") || string.equals("out"))
            return Optional.of(OUTPUT);
        if (string.equals("input_output") || string.equals("io"))
            return Optional.of(INPUT_OUTPUT);
        return Optional.empty();
    }

}
