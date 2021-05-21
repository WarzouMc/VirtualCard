package fr.warzou.virtualcard.api.core.logger;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

class LogDispatcher extends Thread {

    private final CardLogger logger;

    private BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    public LogDispatcher(@NotNull CardLogger logger) {
        this.logger = logger;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            LogRecord logRecord = null;
            try {
                logRecord = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.logger.doLog(logRecord);
        }
    }

    protected synchronized void put(LogRecord logRecord) {
        if (!isInterrupted())
            this.queue.add(logRecord);
    }

    protected BlockingQueue<LogRecord> getQueue() {
        return this.queue;
    }
}
