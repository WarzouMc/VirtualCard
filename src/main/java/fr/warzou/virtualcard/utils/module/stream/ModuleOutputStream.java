package fr.warzou.virtualcard.utils.module.stream;

import fr.warzou.virtualcard.utils.module.stream.reader.Reader;
import org.jetbrains.annotations.NotNull;

public interface ModuleOutputStream extends ModuleStream {

    <T> Reader<T> read(@NotNull String key, @NotNull Class<T> type);

    default Reader<?> read(@NotNull String key) {
        return read(key, Object.class);
    }

    default Reader<Byte> readByte(@NotNull String key) {
        return read(key, Byte.class);
    }

    default Reader<Integer> readInt(@NotNull String key) {
        return read(key, Integer.class);
    }

    default Reader<Long> readLong(@NotNull String key) {
        return read(key, Long.class);
    }

    default Reader<Double> readDouble(@NotNull String key) {
        return read(key, Double.class);
    }

    default Reader<Float> readFloat(@NotNull String key) {
        return read(key, Float.class);
    }

    default Reader<Character> readChar(@NotNull String key) {
        return read(key, Character.class);
    }

    default Reader<String> readString(@NotNull String key) {
        return read(key, String.class);
    }

    default Reader<Boolean> readBoolean(@NotNull String key) {
        return read(key, Boolean.class);
    }

    default Reader<Object> readObject(@NotNull String key) {
        return read(key, Object.class);
    }

}
