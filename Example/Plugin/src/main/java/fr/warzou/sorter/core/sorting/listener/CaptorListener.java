package fr.warzou.sorter.core.sorting.listener;

import fr.warzou.sorter.core.sorting.SortInterpreter;
import fr.warzou.sorter.core.sorting.stats.SorterStats;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.events.events.captor.CaptorCaptEvent;
import fr.warzou.virtualcard.core.modules.captor.Captor;
import fr.warzou.virtualcard.utils.event.utils.EventHandler;
import fr.warzou.virtualcard.utils.event.utils.EventListener;

public class CaptorListener implements EventListener {

    private final SorterStats sorterStats;

    public CaptorListener(SorterStats sorterStats) {
        this.sorterStats = sorterStats;
    }

    @EventHandler
    public void onCaptorCapt(CaptorCaptEvent event) {
        if (event.isNull())
            return;

        Captor captor = event.getSource();
        if (!captor.equals(this.sorterStats.getInterpreter().getCaptor()))
            return;
        Captor.CaptResult result = event.getResult();
        this.sorterStats.getInterpreter().interpret(result);
    }
}
