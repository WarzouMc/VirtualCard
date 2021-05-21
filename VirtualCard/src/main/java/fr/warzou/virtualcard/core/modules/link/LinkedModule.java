package fr.warzou.virtualcard.core.modules.link;

import fr.warzou.virtualcard.utils.module.ModuleBase;
import org.jetbrains.annotations.NotNull;

public class LinkedModule {

    private final ModuleBase<?> moduleBase;
    private final Point point;

    public LinkedModule(@NotNull ModuleBase<?> moduleBase, Point point) {
        this.moduleBase = moduleBase;
        this.point = point;
    }

    @NotNull
    public ModuleBase<?> getModuleBase() {
        return this.moduleBase;
    }

    public Point getPoint() {
        return this.point;
    }

    @Override
    public String toString() {
        return "LinkedModule{" +
                "moduleBase=" + moduleBase +
                ", point=" + point +
                '}';
    }

    public enum Face {
        LEFT, RIGHT,
        FRONT, BEHIND,
        UP, DOWN
    }
}
