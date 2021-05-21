package fr.warzou.virtualcard.api.core.logger;

import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

enum LevelColor {

    INFO(Ansi.Color.WHITE),
    OFF(Ansi.Color.MAGENTA),
    SEVERE(Ansi.Color.RED),
    WARNING(Ansi.Color.YELLOW),
    CONFIG(Ansi.Color.DEFAULT),
    FINE(Ansi.Color.GREEN),
    FINER(Ansi.Color.GREEN),
    FINEST(Ansi.Color.GREEN),
    ALL(Ansi.Color.DEFAULT);


    private final Ansi.Color color;

    LevelColor(Ansi.Color color) {
        this.color = color;
    }

    public Ansi.Color getColor() {
        return this.color;
    }

    protected static LevelColor fromLevel(@NotNull Level level) {
        String name = level.getName();
        return valueOf(name.toUpperCase());
    }
}
