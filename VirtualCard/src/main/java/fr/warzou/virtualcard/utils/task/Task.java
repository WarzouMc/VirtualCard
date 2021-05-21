package fr.warzou.virtualcard.utils.task;

import java.util.function.Consumer;

/**
 * Juste a unit of execution
 * @version 1.5.2
 * @author Redstonneur1256, Warzou
 * @param <T> Task value class type
 */
public interface Task<T> {

    /**
     * Complete the task for a given value.
     * @param value value to complete
     */
    void complete(T value);

    /**
     * This methode allow to replace the old {@link Consumer} by a new consumer.
     * <p>This consumer is call during the completion</p>
     * <p>Default consumer : </p>{@code t -> {};}
     * @param consumer new consumer
     */
    void whenComplete(Consumer<T> consumer);

    /**
     * This method return this task result.
     * <p>If task is not finish that wait the end to return a value.</p>
     * @see Task#get(int)
     * @return the task result
     */
    default T get() {
        return get(-1);
    }

    /**
     * Return task result is this task is done, if this is not done that return the orElse param.
     * @see Task#getOrElse(Object, int)
     * @param orElse value at return is task is not done
     * @return task result is task is done, orElse if not
     */
    default T getOrElse(T orElse) {
        return getOrElse(orElse, 0);
    }

    /**
     * Return task result is this task is done after the given time, if this is not done that return the orElse param.
     * @param orElse value at return is task is not done
     * @param timeout max waiting time in millis
     * @return task result is task is done, orElse if not
     */
    T getOrElse(T orElse, int timeout);

    /**
     * Return task result is this task is done after the given time.
     * @param timeout max waiting time in millis
     * @return task result if is done
     */
    T get(int timeout);

    /**
     * This method return the current task activity.
     * @return true if task is done and false else
     */
    boolean isDone();

}
