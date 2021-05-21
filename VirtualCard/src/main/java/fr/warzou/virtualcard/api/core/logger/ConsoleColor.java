package fr.warzou.virtualcard.api.core.logger;

import org.fusesource.jansi.Ansi;

public final class ConsoleColor {

    public static final String RESET = Ansi.ansi().reset().toString();

    public static final String BOLD = Ansi.ansi().bold().toString();
    public static final String BOLD_OFF = Ansi.ansi().boldOff().toString();

    public static final String BLINK_1 = Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString();
    public static final String BLINK_2 = Ansi.ansi().a(Ansi.Attribute.BLINK_FAST).toString();
    public static final String BLINK_OFF = Ansi.ansi().a(Ansi.Attribute.BLINK_OFF).toString();

    public static final String UNDERLINE = Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString();
    public static final String UNDERLINE_DOUBLE = Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString();
    public static final String UNDERLINE_OFF = Ansi.ansi().a(Ansi.Attribute.UNDERLINE_OFF).toString();

    public static final String STRIKETHROUGH = Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString();
    public static final String STRIKETHROUGH_OFF = Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_OFF).toString();

    public static final String ITALIC = Ansi.ansi().a(Ansi.Attribute.ITALIC).toString();
    public static final String ITALIC_OFF = Ansi.ansi().a(Ansi.Attribute.ITALIC_OFF).toString();

    public static final String BACKGROUND_BLACK = Ansi.ansi().bg(Ansi.Color.BLACK).toString();
    public static final String BACKGROUND_RED = Ansi.ansi().bg(Ansi.Color.RED).toString();
    public static final String BACKGROUND_GREEN = Ansi.ansi().bg(Ansi.Color.GREEN).toString();
    public static final String BACKGROUND_YELLOW = Ansi.ansi().bg(Ansi.Color.YELLOW).toString();
    public static final String BACKGROUND_BLUE = Ansi.ansi().bg(Ansi.Color.BLUE).toString();
    public static final String BACKGROUND_MAGENTA = Ansi.ansi().bg(Ansi.Color.MAGENTA).toString();
    public static final String BACKGROUND_CYAN = Ansi.ansi().bg(Ansi.Color.CYAN).toString();
    public static final String BACKGROUND_WHITE = Ansi.ansi().bg(Ansi.Color.WHITE).toString();
    public static final String BACKGROUND_DEFAULT = Ansi.ansi().bg(Ansi.Color.DEFAULT).toString();

    public static final String FOREGROUND_BLACK = Ansi.ansi().fg(Ansi.Color.BLACK).toString();
    public static final String FOREGROUND_RED = Ansi.ansi().fg(Ansi.Color.RED).toString();
    public static final String FOREGROUND_GREEN = Ansi.ansi().fg(Ansi.Color.GREEN).toString();
    public static final String FOREGROUND_YELLOW = Ansi.ansi().fg(Ansi.Color.YELLOW).toString();
    public static final String FOREGROUND_BLUE = Ansi.ansi().fg(Ansi.Color.BLUE).toString();
    public static final String FOREGROUND_MAGENTA = Ansi.ansi().fg(Ansi.Color.MAGENTA).toString();
    public static final String FOREGROUND_CYAN = Ansi.ansi().fg(Ansi.Color.CYAN).toString();
    public static final String FOREGROUND_WHITE = Ansi.ansi().fg(Ansi.Color.WHITE).toString();
    public static final String FOREGROUND_DEFAULT = Ansi.ansi().fg(Ansi.Color.DEFAULT).toString();

}
