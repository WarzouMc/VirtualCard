package fr.warzou.virtualcard.utils.task.impl;

import fr.warzou.virtualcard.utils.task.Task;
import fr.warzou.virtualcard.utils.task.ThreadPool;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Implementation of {@link ThreadPool} interface.
 * <p>{@link QueuedThreadPool#perform(Runnable)} add a the runnable in {@link QueuedThreadPool#queue} and perform the queue element by element.</p>
 * @version 1.5.2
 * @author Redstonneur1256, Warzo
 */
public class QueuedThreadPool implements ThreadPool {

    private final ThreadFactory threadFactory;
    /**
     * Runnable queue
     */
    private final ConcurrentLinkedQueue<Runnable> queue;
    /**
     * Current worker
     */
    private Worker worker;
    /**
     * Worker thread name
     */
    private final String workerName;

    /**
     * Create a new instance of {@link QueuedThreadPool}.
     * <p>Thread daemon is on true with this constructor</p>
     * <p>See {@link QueuedThreadPool#QueuedThreadPool(String, boolean)}</p>
     * @param workerName This is the name take by the worker thread.
     */
    public QueuedThreadPool(String workerName) {
        this(workerName, true);
    }

    /**
     * Create a new instance of {@link QueuedThreadPool}.
     * <p>That create a {@link ThreadFactory}</p>
     * <p>See {@link QueuedThreadPool#QueuedThreadPool(ThreadFactory, String)} to create self {@link ThreadFactory}</p>
     * @param workerName This is the name take by the worker thread.
     * @param daemon Thread daemon value, see {@link Thread#setDaemon(boolean)}
     */
    public QueuedThreadPool(String workerName, boolean daemon) {
        this(r -> {
            Consumer<Thread> threadConsumer = thread -> r.run();
            Thread thread = new Thread(() -> threadConsumer.accept(Thread.currentThread()));
            thread.setDaemon(daemon);
            thread.start();
            return thread;
        }, workerName);
    }

    /**
     * Create a new instance of {@link QueuedThreadPool}.
     * @param threadFactory {@link Worker} {@link ThreadFactory}
     * @param workerName This is the name take by the worker thread.
     */
    public QueuedThreadPool(ThreadFactory threadFactory, String workerName) {
        this.threadFactory = threadFactory;
        this.queue = new ConcurrentLinkedQueue<>();
        this.workerName = workerName;
    }

    @Override
    public <T> Task<T> perform(Supplier<T> supplier) {
        Task<T> task = new ImplTask<>();
        perform(() -> task.complete(supplier.get()));
        return task;
    }

    @Override
    public void perform(Runnable runnable) {
        if (this.worker == null) {
            this.worker = new Worker(this);
            this.worker.start();
        }
        this.worker.next(runnable);
    }

    @Override
    public void stop() {
        if (this.worker == null)
            return;
        this.worker.stop();
    }

    /**
     * That return the {@link QueuedThreadPool#threadFactory}
     * @return field threadFactory
     */
    protected ThreadFactory getThreadFactory() {
        return this.threadFactory;
    }

    /**
     * That return the current {@link Queue} of {@link Runnable}
     * @return field queue
     */
    protected Queue<Runnable> getRunnableQueue() {
        return this.queue;
    }

    @Override
    public boolean isActive() {
        synchronized (this.worker.lock) {
            return this.worker.runnable != null;
        }
    }

    /**
     * That return the current thread name
     * @return field workerName
     */
    protected String getWorkerName() {
        return workerName;
    }

    /**
     * Implementation of {@link AbstractTask}
     * @version 1.5.2
     * @author Redstonneur1256, Warzou
     * @param <T> Task value class type
     */
    protected static class ImplTask<T> extends AbstractTask<T> {

        @Override
        protected void complete() {
            synchronized (this.lock) {
                ThreadFactory threadFactory = r -> {
                    Consumer<Thread> threadConsumer = thread -> r.run();
                    Thread thread = new Thread(() -> threadConsumer.accept(Thread.currentThread()));
                    thread.setDaemon(true);
                    thread.start();
                    return thread;
                };

                if (this.consumer == null)
                    this.consumer = t -> {};

                Consumer<T> consumer = item -> threadFactory.newThread(() -> this.consumer.accept(this.value));
                consumer.accept(this.value);

                this.done = true;
                this.lock.notifyAll();
            }
        }

    }
}
