package fr.warzou.virtualcard.api.core.ticktask;

import fr.warzou.virtualcard.api.Card;

public abstract class CardRunnable {

    private boolean active;
    private final int id;

    public CardRunnable() {
        this.id = CardTick.TaskManager.count++;
    }

    protected abstract void run();

    public void cancel() {
        this.active = false;
    }

    protected void setActive() {
        this.active = true;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getID() {
        return this.id;
    }

    public CardTask runTaskTimer(Card card) {
        return runTaskTimer(card, 0);
    }

    public CardTask runTaskTimer(Card card, int delay) {
        return runTaskTimer(card, delay, 1);
    }

    public CardTask runTaskTimer(Card card, int delay, int period) {
        return runTask(card, delay, period, -1);
    }

    public CardTask runTaskLater(Card card, int delay) {
        return runTask(card, delay, 1, 0);
    }

    public CardTask runTask(Card card, int delay, int period, int repeat) {
        return card.getCardTick().runTask(this, delay, period, repeat);
    }

}
