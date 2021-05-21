package fr.warzou.sorter.main;

import fr.warzou.sorter.core.commands.start.StartSortCommand;
import fr.warzou.sorter.core.modules.ModuleInitializer;
import fr.warzou.sorter.core.sorting.listener.CaptorListener;
import fr.warzou.sorter.core.sorting.listener.PusherListener;
import fr.warzou.sorter.core.sorting.stats.SorterStats;
import fr.warzou.virtualcard.api.Card;

public class SorterManager {

    private final Card card;
    private final SorterStats sorterStats;

    public SorterManager(Card card) {
        this.card = card;
        this.sorterStats = new SorterStats(this.card);
    }

    protected void initModules() {
        new ModuleInitializer(this.card).init();
    }

    protected void initListeners() {
        this.card.getEventManager().registerListener(new CaptorListener(this.sorterStats));
        this.card.getEventManager().registerListener(new PusherListener());
    }

    protected void initCommands() {
        this.card.getCommandRegister().getCommand("startsort").setExecutor(new StartSortCommand(this.sorterStats));
    }
}
