package fr.warzou.virtualcard.api.environment.ticktask;

public abstract class CardRunnable {

    private boolean active;
    private final int id;

    public CardRunnable() {
        this.id = CardTick.TaskManager.count++;
    }

    public abstract void run();

    public void cancel() {
        this.active = false;
    }

    public void setActive() {
        this.active = true;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getId() {
        return this.id;
    }
}
