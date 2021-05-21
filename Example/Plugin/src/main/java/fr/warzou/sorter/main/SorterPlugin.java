package fr.warzou.sorter.main;

import fr.warzou.virtualcard.api.core.plugin.CardPlugin;
import fr.warzou.virtualcard.utils.event.utils.EventListener;

public class SorterPlugin extends CardPlugin implements EventListener {

    @Override
    public void onStart() {
        super.onStart();
        SorterManager manager = new SorterManager(getCard());
        manager.initModules();
        manager.initCommands();
        manager.initListeners();
    }
}
