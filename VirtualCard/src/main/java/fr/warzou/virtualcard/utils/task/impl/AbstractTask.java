package fr.warzou.virtualcard.utils.task.impl;

import fr.warzou.virtualcard.utils.task.Task;

import java.util.function.Consumer;

/**
 * Implementation of {@link Task} interface.
 * @version 1.5.2
 * @author Redstonneur1256, Warzou
 * @param <T> Task value class type
 */
abstract class AbstractTask<T> implements Task<T> {

    /**
     * Lock object
     */
    protected final Object lock = new Object();
    protected boolean done;
    protected T value;
    protected Consumer<T> consumer;

    @Override
    public void complete(T value) {
        synchronized (this.lock) {
            if (this.done)
                throw new IllegalStateException("This task is already done !");
            this.value = value;
            complete();
        }
    }

    /**
     * Complete the {@link AbstractTask#consumer}
     */
    protected abstract void complete();

    @Override
    public void whenComplete(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    @Override
    public T getOrElse(T orElse, int timeout) {
        timeout = Math.abs(timeout);
        T value = get(timeout);
        return this.done ? value : orElse;
    }

    @Override
    public T get(int timeout) {
        if (isDone())
            return this.value;
        synchronized (this.lock) {
            Runnable wait = () -> {
                try {
                    if (timeout < 0)
                        this.lock.wait();
                    this.lock.wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            wait.run();
            return this.value;
        }
    }

    @Override
    public boolean isDone() {
        synchronized (this.lock) {
            return this.done;
        }
    }
}
