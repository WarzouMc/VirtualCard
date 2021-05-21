package fr.warzou.virtualcard.api.core.ticktask;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.core.modules.CardClock;
import fr.warzou.virtualcard.utils.task.ThreadPool;
import fr.warzou.virtualcard.utils.task.impl.ExecutorThreadPool;
import fr.warzou.virtualcard.utils.task.impl.QueuedThreadPool;

class CardRunnableProperties {

    private final ThreadPool self;
    private final CardRunnable runnable;
    private int delay;
    private int period;
    private int repeat;

    private int currentCount = 0;

    private final CardTask task;
    private final long startTick;

    protected CardRunnableProperties(Card card, CardRunnable runnable, int delay, int period, int repeat) {
        this.self = new ExecutorThreadPool("Card-Task::" + runnable.getID(), false, 1);
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
        this.repeat = repeat;

        this.task = new CardTask() {
            @Override
            public Card source() {
                return card;
            }

            @Override
            public boolean isActive() {
                return runnable.isActive();
            }

            @Override
            public int getID() {
                return runnable.getID();
            }

            @Override
            public void cancel() {
                self.stop();
                runnable.cancel();
            }
        };
        this.startTick = CardClock.system().nowTicks();
        this.runnable.setActive();
    }

    protected CardTask getTask() {
        return this.task;
    }

    protected void setDelay(int delay) {
        this.delay = delay;
    }

    protected void setPeriod(int period) {
        this.period = period;
    }

    protected void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    protected CardRunnable getRunnable() {
        return this.runnable;
    }

    protected int getDelay() {
        return this.delay;
    }

    protected int getPeriod() {
        return this.period;
    }

    public int getRepeat() {
        return this.repeat;
    }

    protected void run() {
        long nowTicks = CardClock.system().nowTicks();
        if (this.repeat >= 0 && this.repeat - this.currentCount < 0) {
            this.runnable.cancel();
            return;
        }
        if ((nowTicks - this.startTick) % this.period != 0)
            return;
        if (nowTicks - this.startTick < this.delay)
            return;
        this.currentCount++;
        self.perform(runnable::run);
    }
}
