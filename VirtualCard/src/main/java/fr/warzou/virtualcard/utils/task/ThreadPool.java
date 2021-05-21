package fr.warzou.virtualcard.utils.task;

import java.util.function.Supplier;

/**
 * Used to create concurrent tasks.
 * @version 1.5.2
 * @author Redstonneur1256, Warzou
 */
public interface ThreadPool {

    /**
     * This method allow to perform a {@link Supplier}.
     * <p>Its return a {@link Task} and call {@link #perform(Runnable)}</p>
     * {@code () -> task.complete(supplier.get())}
     * @param supplier function to execute
     * @param <T> Class type
     * @return the created task
     */
    <T> Task<T> perform(Supplier<T> supplier);

    /**
     * Perform a {@link Runnable}
     * @param runnable {@link Runnable} who need to be execute
     */
    void perform(Runnable runnable);

    /**
     * Shutdown the current task.
     */
    void stop();

    /**
     * This method return the current activity of this task.
     * @return true if task is active, and false if not
     */
    boolean isActive();

}
