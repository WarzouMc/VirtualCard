package fr.warzou.virtualcard.utils.task;

import java.util.function.Supplier;

/**
 * @author Redstonneur1256, Warzou
 */
public interface ThreadPool {

    <T> Task<T> perform(Supplier<T> supplier);

    void perform(Runnable runnable);

    void stop();

    boolean isActive();

}
