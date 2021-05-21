package fr.warzou.sorter.core.modules;

import fr.warzou.sorter.core.modules.container.IndexedContainer;
import fr.warzou.sorter.core.modules.pusher.IndexedPusher;
import fr.warzou.sorter.core.sorting.stats.SorterStats;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.core.modules.container.container.Container;
import fr.warzou.virtualcard.core.modules.container.dropper.Dropper;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.property.Property;

import java.util.Optional;

public class ModuleInitializer {

    private final Card card;

    public ModuleInitializer(Card card) {
        this.card = card;
    }

    public void init() {
        initContainer();
        initPusher();

        initContainersStats();
    }

    private void initContainer() {
        new IndexedContainer("1", "modules/container1/container.json").init(card);
        new IndexedContainer("2", "modules/container2/container.json").init(card);
        new IndexedContainer("3", "modules/container3/container.json").init(card);
    }

    private void initPusher() {
        new IndexedPusher("1", "modules/pusher1/pusher.json").init(card);
        new IndexedPusher("2", "modules/pusher2/pusher.json").init(card);
        new IndexedPusher("3", "modules/pusher3/pusher.json").init(card);
    }

    private void initContainersStats() {
        ModuleManager moduleManager = this.card.getModuleManager();
        moduleManager.getModuleMap().entries().filter(Container.class).forEach(containerSinglePropertyEntry -> {
            Property<Container> property = containerSinglePropertyEntry.value();
            Container container = property.value();
            container.setCapacity(SorterStats.SORT_QUEUE_LENGTH);
        });
        Optional<ModuleBase<?>> optionalModuleBase = moduleManager.getModule("dropper");
        if (!optionalModuleBase.isPresent())
            return;
        ModuleBase<?> moduleBase = optionalModuleBase.get();
        if (!(moduleBase instanceof Dropper))
            return;
        Dropper dropper = (Dropper) moduleBase;
        dropper.setCapacity(SorterStats.SORT_QUEUE_LENGTH + 1);
    }
}
