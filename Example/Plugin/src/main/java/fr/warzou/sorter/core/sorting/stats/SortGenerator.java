package fr.warzou.sorter.core.sorting.stats;

import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.api.core.ticktask.CardRunnable;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.captor.Captor;
import fr.warzou.virtualcard.core.modules.container.conveyor.Conveyor;
import fr.warzou.virtualcard.core.modules.container.dropper.Dropper;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import org.fusesource.jansi.Ansi;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class SortGenerator {

    private final SorterStats sorterStats;

    protected SortGenerator(SorterStats sorterStats) {
        this.sorterStats = sorterStats;
    }

    protected void generate(int delay) {
        if (delay == 0) {
            rawGenerate();
            return;
        }

        new CardRunnable() {
            @Override
            protected void run() {
                rawGenerate();
            }
        }.runTaskLater(this.sorterStats.getCard(), delay);
    }

    private void rawGenerate() {
        ModuleManager moduleManager = this.sorterStats.getCard().getModuleManager();
        Optional<ModuleBase<?>> optional = moduleManager.getModule("dropper");
        if (!optional.isPresent()) {
            System.err.println("Couldn't get 'dropper' module !");
            return;
        }
        ModuleBase<?> moduleBase = optional.get();
        if (!(moduleBase instanceof Dropper)) {
            System.err.println("'dropper' module is not instance of " + Dropper.class);
            return;
        }

        Dropper dropper = (Dropper) moduleBase;
        Item<Integer>[] array = new Item[SorterStats.SORT_QUEUE_LENGTH];
        for (int i = 0; i < SorterStats.SORT_QUEUE_LENGTH; i++) {
            int random = ThreadLocalRandom.current().nextInt(1000);
            array[i] = new Item<>(Integer.class, random);
        }
        dropper.fill(array);

        System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Sort is init and started !");
        System.out.println("Sort array of " + array.length + " items :");
        List<Integer> list = Arrays.stream(array).map(Item::getItem).collect(Collectors.toList());
        System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).toString() + list);

        Captor captor = this.sorterStats.getInterpreter().getCaptor();

        optional = this.sorterStats.getCard().getModuleManager().getModule("conveyor");
        if (!optional.isPresent()) {
            System.err.println("Couldn't find conveyor module !");
            this.sorterStats.getCard().stop();
            return;
        }

        Conveyor conveyor = (Conveyor) optional.get();
        System.out.println("Captor parameters : ");
        System.out.println("Constants : " + Arrays.toString(captor.constants()));
        System.out.println("Comparator : " + captor.comparator());
        System.out.println("######################################");
        System.out.println("Sort predictions : ");
        System.out.println("Time : " + (list.size() + conveyor.length()) * conveyor.updateTicks() + " ticks");
        System.out.println("######################################");
    }
}
