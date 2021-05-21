package fr.warzou.virtualcard.utils.task.impl;

import java.util.concurrent.ThreadFactory;

/**
 * Juste run element of the {@link QueuedThreadPool} queue.
 * @version 1.5.2
 * @author Redstonneur1256, Warzou
 */
class Worker {

    /**
     * Lock object
     */
    protected final Object lock;
    private final QueuedThreadPool queuedThreadPool;
    /**
     * Worker id
     */
    private final int id;
    private final ThreadFactory threadFactory;

    /**
     * Worker {@link Thread}
     */
    private Thread thread;
    /**
     * Current target {@link Runnable}
     */
    protected Runnable runnable;

    /**
     * Create a new instance of Worker.
     * @param threadPool {@link fr.warzou.virtualcard.utils.task.ThreadPool} source
     */
    Worker(QueuedThreadPool threadPool) {
        this.lock = new Object();
        this.queuedThreadPool = threadPool;
        this.id = Math.toIntExact(Math.round(Math.random() * 10000));
        this.threadFactory = threadPool.getThreadFactory();
    }

    /**
     * Start this Worker
     */
    protected void start() {
        this.thread = this.threadFactory.newThread(this::run);
        this.thread.setName("Worker::" + this.queuedThreadPool.getWorkerName() + "-" + this.id);
    }

    /**
     * That add a runnable in the queue if {@code this.runnable != null}, and else that set {@link Worker#runnable} to the param runnable
     * @param runnable new runnable
     */
    protected void next(Runnable runnable) {
        synchronized (this.lock) {
            if (this.runnable != null) {
                this.queuedThreadPool.getRunnableQueue().add(runnable);
                return;
            }
            this.runnable = runnable;
            this.lock.notifyAll();
        }
    }

    /**
     * Stop this Worker
     */
    protected void stop() {
        this.thread.interrupt();
    }

    /**
     * Worker loop
     */
    private void run() {
        while (this.thread != null && !this.thread.isInterrupted()) {
            synchronized (this.lock) {
                if (this.runnable == null) {
                    Runnable wait = () -> {
                        try {
                            this.lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                    wait.run();
                }
                this.runnable.run();
                this.runnable = null;

                synchronized (this.queuedThreadPool.getRunnableQueue()) {
                    Runnable queue;
                    if ((queue = this.queuedThreadPool.getRunnableQueue().poll()) == null)
                        continue;
                    next(queue);
                }
            }
        }
    }
}
