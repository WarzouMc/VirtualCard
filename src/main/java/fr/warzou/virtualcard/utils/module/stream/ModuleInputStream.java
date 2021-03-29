package fr.warzou.virtualcard.utils.module.stream;

import fr.warzou.virtualcard.utils.module.stream.writer.Writer;
import org.jetbrains.annotations.NotNull;

public interface ModuleInputStream extends ModuleStream {

    <T> Writer<T> write(@NotNull String key, @NotNull Class<T> type);

    default Writer<?> write(@NotNull String key) {
        return write(key, Object.class);
    }

    default Writer<Byte> writeByte(@NotNull String key) {
        return write(key, Byte.class);
    }

    default Writer<Integer> writeInt(@NotNull String key) {
        return write(key, Integer.class);
    }

    default Writer<Long> writeLong(@NotNull String key) {
        return write(key, Long.class);
    }

    default Writer<Double> writeDouble(@NotNull String key) {
        return write(key, Double.class);
    }

    default Writer<Float> writeFloat(@NotNull String key) {
        return write(key, Float.class);
    }

    default Writer<Character> writeChar(@NotNull String key) {
        return write(key, Character.class);
    }

    default Writer<String> writeString(@NotNull String key) {
        return write(key, String.class);
    }

    default Writer<Boolean> writeBoolean(@NotNull String key) {
        return write(key, Boolean.class);
    }

    default Writer<Object> writeObject(@NotNull String key) {
        return write(key, Object.class);
    }

}
