package fr.warzou.virtualcard.utils.module.stream.reader;

import java.util.Optional;

public interface Reader<T> {

    String key();

    Class<T> type();

    Optional<T> read();

}
