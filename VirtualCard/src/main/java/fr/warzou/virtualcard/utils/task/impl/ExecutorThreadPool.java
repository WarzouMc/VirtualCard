package fr.warzou.virtualcard.utils.task.impl;

import fr.warzou.virtualcard.utils.task.Task;
import fr.warzou.virtualcard.utils.task.ThreadPool;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * Just an implementation of {@link ThreadPool}
 * @version 1.5.2
 * @author Redstonneur1256, Warzou
 */
public class ExecutorThreadPool implements ThreadPool {

    private final ExecutorService executorService;

    /**
     * Create a new instance of {@link ExecutorThreadPool}
     * @param name thread name
     * @param daemon thread daemon value
     * @param threads number of threads in the pool
     */
    public ExecutorThreadPool(@NotNull String name, boolean daemon, int threads) {
        this.executorService = Executors.newFixedThreadPool(threads,
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setDaemon(daemon);
                    thread.setName(name + "::" + Math.round(Math.random() * Math.pow(10, 10)));
                    return thread;
                });
    }

    @Override
    public <T> Task<T> perform(Supplier<T> supplier) {
        Task<T> task = new QueuedThreadPool.ImplTask<>();
        perform(() -> task.complete(supplier.get()));
        return task;
    }

    @Override
    public void perform(Runnable runnable) {
        this.executorService.submit(runnable);
    }

    @Override
    public void stop() {
        this.executorService.shutdown();
    }

    @Override
    public boolean isActive() {
        return !this.executorService.isShutdown();
    }
}
