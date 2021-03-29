package fr.warzou.virtualcard.api.environment.ticktask;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.task.ThreadPool;
import fr.warzou.virtualcard.utils.task.impl.ExecutorThreadPool;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CardTick {

    public static final double TICK = 0.05;
    private final TaskManager manager;

    public CardTick(Card card) {
        this.manager = new TaskManager();
        new Tick(card, this.manager);
    }

    public CardRunnable runTaskTimer(CardRunnable runnable) {
        return runTaskTimer(runnable, 0);
    }

    public CardRunnable runTaskTimer(CardRunnable runnable, int delay) {
        return runTaskTimer(runnable, delay, 1);
    }

    public CardRunnable runTaskTimer(CardRunnable runnable, int delay, int period) {
        this.manager.activate(runnable, delay, period);
        return runnable;
    }

    private static class Tick extends TimerTask {

        private final Object lock;

        private final ThreadPool threadPool;
        private final TaskManager manager;
        private final TickLog tickLog;

        public Tick(Card card, TaskManager manager) {
            this.manager = manager;
            this.tickLog = new TickLog();
            this.threadPool = new ExecutorThreadPool("card::tick_thread", false, 1);
            this.lock = new Object();
            new Timer().scheduleAtFixedRate(this, 0, Math.round(TICK * 1000));

            new ExecutorThreadPool("card::tick_executor", false, 1).perform(() -> syncTick(card));
        }

        @Override
        public void run() {
            synchronized (this.lock) {
                this.lock.notifyAll();
            }
        }

        private void syncTick(Card card) {
            while (card.isEnable()) {
                synchronized (this.lock) {
                    try {
                        this.lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.tickLog.log();
                    for (CardRunnable task : new ArrayList<>(this.manager.activeTask)) {
                        if (!task.isActive()) {
                            this.manager.finishTask(task.getId());
                            continue;
                        }
                        this.threadPool.perform(task::run);
                    }
                }
            }
        }
    }

    protected static class TaskManager {

        protected static int count = 0;
        private final List<CardRunnable> activeTask;

        private TaskManager() {
            this.activeTask = new ArrayList<>();
        }

        private void activate(CardRunnable runnable, int delay, int period) {
            runnable.setActive();
            this.activeTask.add(runnable);
        }

        private void finishTask(int id) {
            Optional<CardRunnable> optionalCardRunnable = this.activeTask.stream().filter(runnable -> runnable.getId() == id)
                    .findFirst();
            if (!optionalCardRunnable.isPresent())
                throw new NoSuchElementException();
            CardRunnable cardRunnable = optionalCardRunnable.get();
            this.activeTask.remove(cardRunnable);
        }

    }

    private static class TickLog {

        private final List<Integer> tickLogList = new ArrayList<>();
        private long step = System.currentTimeMillis();
        private int tickCount = 0;

        private void log() {
            long now = System.currentTimeMillis();
            if (now - this.step >= 1000) {
                this.step = System.currentTimeMillis();
                this.tickLogList.add(this.tickCount);
                this.tickCount = 0;

                print();
                return;
            }
            this.tickCount++;
        }

        private void print() {
            AtomicInteger fullTickCount = new AtomicInteger();
            this.tickLogList.forEach(fullTickCount::addAndGet);
            System.out.println((0.0 + fullTickCount.get()) / this.tickLogList.size() + " ticks/s");
        }

    }

}
