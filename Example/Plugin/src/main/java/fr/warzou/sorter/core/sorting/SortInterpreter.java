package fr.warzou.sorter.core.sorting;

import fr.warzou.sorter.core.sorting.stats.SorterStats;
import fr.warzou.virtualcard.core.modules.captor.Captor;
import fr.warzou.virtualcard.core.modules.container.conveyor.Conveyor;
import fr.warzou.virtualcard.core.modules.link.LinkedModule;
import fr.warzou.virtualcard.core.modules.link.UniqueCaptorLinkable;
import fr.warzou.virtualcard.core.modules.pusher.Pusher;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SortInterpreter {

    private final SorterStats sorterStats;
    private final Captor captor;
    private int captorPosition;
    private int pushers;
    private List<LinkedModule> pusherList;

    public SortInterpreter(SorterStats sorterStats, Captor captor) {
        this.sorterStats = sorterStats;
        this.captor = captor;
        try {
            init();
        } catch (IllegalClassFormatException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IllegalClassFormatException, IllegalStateException {
        UniqueCaptorLinkable captorLinkable = this.captor.link();
        if (!(captorLinkable instanceof Conveyor))
            throw new IllegalClassFormatException("Couldn't init interpreter if captor isn't on a Conveyor !");
        this.captorPosition = captorLinkable.captQueue();
        Object[] constant = captor.constants();
        this.pushers = constant.length;
        Conveyor conveyor = (Conveyor) captorLinkable;
        LinkedModule[] allLinkedModule = conveyor.linkedModules();
        List<LinkedModule> behindModules = Arrays.stream(allLinkedModule)
                .filter(linkedModule -> linkedModule.getPoint().face() == LinkedModule.Face.FRONT)
                .collect(Collectors.toList());
        List<LinkedModule> pushers = behindModules.stream()
                .filter(linkedModule -> (linkedModule.getModuleBase() instanceof Pusher)
                        && linkedModule.getPoint().position() > this.captorPosition)
                .collect(Collectors.toList());
        if (pushers.size() < this.pushers)
            throw new IllegalStateException("Conveyor need, at least, " + this.pushers + " pushers on face " + LinkedModule.Face.FRONT +
                    " and next to the position " + this.captorPosition);

        LinkedModule[] linkedPushers = new LinkedModule[this.pushers];
        for (int i = 0; i < this.pushers; i++)
            linkedPushers[i] = pushers.get(i);

        this.pusherList = Arrays.asList(linkedPushers);
    }

    public void interpret(@NotNull Captor.CaptResult captResult) {
        int value = captResult.getValue();
        if (value >= this.pushers)
            return;
        LinkedModule linkedModule = this.pusherList.get(value);
        ModuleBase<?> moduleBase = linkedModule.getModuleBase();
        if (!(moduleBase instanceof Pusher))
            return;
        Pusher pusher = (Pusher) moduleBase;
        int waiting = linkedModule.getPoint().position() - this.captorPosition;
        pusher.addTask(waiting);

        System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Captor has capt : " + captResult.getItem().getItem() +
                ", Result -> " + value);
    }

    public Captor getCaptor() {
        return this.captor;
    }
}
