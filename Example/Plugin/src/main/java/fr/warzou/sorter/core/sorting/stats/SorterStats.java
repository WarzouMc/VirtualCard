package fr.warzou.sorter.core.sorting.stats;

import fr.warzou.sorter.core.sorting.SortInterpreter;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.logger.ConsoleColor;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.api.core.ticktask.CardRunnable;
import fr.warzou.virtualcard.core.modules.CardClock;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.captor.Captor;
import fr.warzou.virtualcard.core.modules.container.container.Container;
import fr.warzou.virtualcard.core.modules.container.conveyor.Conveyor;
import fr.warzou.virtualcard.utils.module.ModuleBase;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SorterStats {

    public final static int SORT_QUEUE_LENGTH = 500;

    private final Card card;

    private boolean start;
    private int updateTick;
    private SortGenerator generator;
    private SortInterpreter interpreter;

    private int sortTimePrediction;

    public SorterStats(Card card) {
        this.card = card;
        this.start = false;
        this.generator = null;
    }

    public void start(int delay) {
        if (this.start)
            return;

        this.start = true;
        Optional<ModuleBase<?>> optional = this.card.getModuleManager().getModule("captor");
        if (!optional.isPresent())
            return;
        ModuleBase<?> moduleBase = optional.get();
        if (!(moduleBase instanceof Captor))
            return;
        this.interpreter = new SortInterpreter(this, (Captor) moduleBase);
        this.generator = new SortGenerator(this);
        this.generator.generate(Math.abs(delay));

        this.updateTick = 10;
        optional = this.card.getModuleManager().getModule("conveyor");
        if (!optional.isPresent())
            return;
        moduleBase = optional.get();

        if (!(moduleBase instanceof Conveyor))
            return;
        this.updateTick = ((Conveyor) moduleBase).updateTicks();
        this.sortTimePrediction = (SORT_QUEUE_LENGTH + ((Conveyor) moduleBase).length()) * this.updateTick;

        new EndLogTask(this).runTaskLater(this.card, this.sortTimePrediction + delay + 20 * 2);
    }

    public boolean isStart() {
        return this.start;
    }

    public int getUpdateTick() {
        return this.updateTick;
    }

    public int getSortTimePrediction() {
        return this.sortTimePrediction;
    }

    public SortGenerator getGenerator() {
        return this.generator;
    }

    public SortInterpreter getInterpreter() {
        return this.interpreter;
    }

    protected Card getCard() {
        return this.card;
    }

    private static class EndLogTask extends CardRunnable {

        private final SorterStats sorterStats;

        private EndLogTask(SorterStats sorterStats) {
            this.sorterStats = sorterStats;
        }

        @Override
        protected void run() {
            System.out.println(ConsoleColor.BACKGROUND_YELLOW + "################");
            System.out.println(ConsoleColor.FOREGROUND_GREEN + "Sorting task is finish !");
            ModuleManager moduleManager = this.sorterStats.getCard().getModuleManager();
            Optional<ModuleBase<?>> optionalFirstContainer = moduleManager.getModule("container.1");
            Optional<ModuleBase<?>> optionalSecondContainer = moduleManager.getModule("container.2");
            Optional<ModuleBase<?>> optionalThirdContainer = moduleManager.getModule("container.3");
            Optional<ModuleBase<?>> optionalTrash = moduleManager.getModule("container");
            if (!(optionalTrash.isPresent() && optionalFirstContainer.isPresent() && optionalSecondContainer.isPresent() && optionalThirdContainer.isPresent())) {
                stopMessage(this.sorterStats.getCard());
                return;
            }

            Container firstContainer = getContainer(optionalFirstContainer.get());
            Container secondContainer = getContainer(optionalSecondContainer.get());
            Container thirdContainer = getContainer(optionalThirdContainer.get());
            Container trash = getContainer(optionalTrash.get());

            List<Object> firstContent = getContent(firstContainer);
            List<Object> secondContent = getContent(secondContainer);
            List<Object> thirdContent = getContent(thirdContainer);
            List<Object> trashContent = getContent(trash);

            System.out.println(ConsoleColor.BACKGROUND_YELLOW + "First container filter content :\n" + ConsoleColor.RESET +
                    ConsoleColor.FOREGROUND_BLUE + firstContent);
            System.out.println(ConsoleColor.BACKGROUND_YELLOW + "Second container filter content :\n" + ConsoleColor.RESET +
                    ConsoleColor.FOREGROUND_CYAN + secondContent);
            System.out.println(ConsoleColor.BACKGROUND_YELLOW + "Third container filter content :\n" + ConsoleColor.RESET +
                    ConsoleColor.FOREGROUND_BLUE + thirdContent);

            System.out.println(ConsoleColor.BACKGROUND_RED + "Non filtered items container :\n" + ConsoleColor.RESET +
                    ConsoleColor.FOREGROUND_MAGENTA + trashContent);

            stopMessage(this.sorterStats.getCard());
        }

        private void stopMessage(Card card) {
            System.out.println(ConsoleColor.BACKGROUND_RED + ConsoleColor.BLINK_2 + "Card will be stop in 5 seconds !");
            CardClock.sleep(5 * 20);
            card.stop();
        }

        private List<Object> getContent(Container container) {
            return container.content().stream().map(Item::getItem).collect(Collectors.toList());
        }

        private Container getContainer(ModuleBase<?> moduleBase) {
            return (Container) moduleBase;
        }
    }
}
