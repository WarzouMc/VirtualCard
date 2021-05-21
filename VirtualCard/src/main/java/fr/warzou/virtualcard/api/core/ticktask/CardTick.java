package fr.warzou.virtualcard.api.core.ticktask;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.core.commands.TickCommand;
import fr.warzou.virtualcard.utils.task.ThreadPool;
import fr.warzou.virtualcard.utils.task.impl.ExecutorThreadPool;
import fr.warzou.virtualcard.utils.task.impl.QueuedThreadPool;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CardTick {

    public static final double TICK = 0.05;
    public static final int TICKS_PER_SECOND = Math.toIntExact(Math.round(1 / TICK));

    private final TaskManager manager;

    public CardTick(Card card) {
        this.manager = new TaskManager(card);
        new Tick(card, this.manager);
    }

    public CardTask runTaskTimer(CardRunnable runnable) {
        return runTaskTimer(runnable, 0);
    }

    public CardTask runTaskTimer(CardRunnable runnable, int delay) {
        return runTaskTimer(runnable, delay, 1);
    }

    public CardTask runTaskTimer(CardRunnable runnable, int delay, int period) {
        return runTask(runnable, delay, period, -1);
    }

    public CardTask runTaskLater(CardRunnable runnable, int delay) {
        return runTask(runnable, delay, 1, 0);
    }

    public CardTask runTask(CardRunnable runnable, int delay, int period, int repeat) {
        return this.manager.activate(runnable, Math.abs(delay), Math.max(period, 1), repeat);
    }

    private static class Tick extends TimerTask {

        private final Object lock;

        private final Card card;
        private final TaskManager manager;
        private final TickLog tickLog;

        public Tick(Card card, TaskManager manager) {
            this.card = card;
            this.manager = manager;
            this.tickLog = new TickLog();
            this.lock = new Object();
            new Timer().scheduleAtFixedRate(this, 0, Math.round(TICK * 1000));

            new QueuedThreadPool("card::tick_executor", false).perform(() -> syncTick(card));
            card.getCommandRegister().getCommand("tick").setExecutor(new TickCommand(this.tickLog));
        }

        @Override
        public void run() {
            synchronized (this.lock) {
                if (!this.card.isEnable()) {
                    shutdownAll();
                    return;
                }
                this.lock.notifyAll();
            }
        }

        private void shutdownAll() {
            System.exit(0);
        }

        private void syncTick(Card card) {
            synchronized (this.lock) {
                while (card.isEnable()) {
                    try {
                        this.lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.tickLog.log();
                    for (CardRunnableProperties properties : new ArrayList<>(this.manager.activeTask)) {
                        CardRunnable task = properties.getRunnable();
                        if (!task.isActive()) {
                            this.manager.finishTask(task.getID());
                            continue;
                        }
                        properties.run();
                    }
                }
            }
        }
    }

    protected static class TaskManager {

        protected static int count = 0;

        private final Card card;
        private final CopyOnWriteArrayList<CardRunnableProperties> activeTask;

        private TaskManager(Card card) {
            this.card = card;
            this.activeTask = new CopyOnWriteArrayList<>();
        }

        private CardTask activate(CardRunnable runnable, int delay, int period, int repeat) {
            CardRunnableProperties cardRunnableProperties = new CardRunnableProperties(this.card, runnable, delay, period, repeat);
            this.activeTask.add(cardRunnableProperties);
            return cardRunnableProperties.getTask();
        }

        private void finishTask(int id) {
            Optional<CardRunnableProperties> optionalCardRunnableProperties = this.activeTask.stream()
                    .filter(runnable -> runnable.getRunnable().getID() == id)
                    .findFirst();
            if (!optionalCardRunnableProperties.isPresent())
                throw new NoSuchElementException();
            CardRunnableProperties cardRunnableProperties = optionalCardRunnableProperties.get();
            this.activeTask.remove(cardRunnableProperties);
        }

    }

    public static class TickLog {

        private int lastTickCount = 0;

        private long step = System.currentTimeMillis();
        private int tickCount = 0;

        private final List<Integer> overallTickLog = new ArrayList<>();
        private final List<Integer> firstRankTickLog = new ArrayList<>();
        private final List<Integer> secondRankTickLog = new ArrayList<>();
        private final List<Integer> thirdRankTickLog = new ArrayList<>();

        private void log() {
            long now = System.currentTimeMillis();
            if (now - this.step >= 1000) {
                this.step = System.currentTimeMillis();
                this.overallTickLog.add(this.tickCount);
                this.firstRankTickLog.add(this.tickCount);
                this.secondRankTickLog.add(this.tickCount);
                this.thirdRankTickLog.add(this.tickCount);
                this.lastTickCount = this.tickCount;
                this.tickCount = 0;

                if (this.overallTickLog.size() > 10)
                    this.firstRankTickLog.remove(0);

                if (this.overallTickLog.size() > 60)
                    this.secondRankTickLog.remove(0);

                if (this.overallTickLog.size() > 5 * 60)
                    this.thirdRankTickLog.remove(0);

                return;
            }
            this.tickCount++;
        }

        public int lastSecond() {
            return this.lastTickCount;
        }

        public double getOverallTickLog() {
            return calculateList(this.overallTickLog);
        }

        public double getFirstRankTickLog() {
            return calculateList(this.firstRankTickLog);
        }

        public double getSecondRankTickLog() {
            return calculateList(this.secondRankTickLog);
        }

        public double getThirdRankTickLog() {
            return calculateList(this.thirdRankTickLog);
        }

        private double calculateList(List<Integer> list) {
            AtomicInteger count = new AtomicInteger();
            list.forEach(count::addAndGet);
            return (0.0 + count.get()) / list.size();
        }
    }

}
