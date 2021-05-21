package fr.warzou.virtualcard.api.core.logger;

import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ConsoleWriterHandler extends Handler {

    private final ConsoleReader reader;

    public ConsoleWriterHandler(@NotNull ConsoleReader reader) {
        this.reader = reader;
    }

    private void print(String message, Ansi.Color color) {
        message = Ansi.ansi().eraseLine(Ansi.Erase.ALL).fg(color).toString() + ConsoleReader.RESET_LINE + message + Ansi.ansi()
                .reset().toString();
        try {
            this.reader.print("\r");
            this.reader.print(message);
            this.reader.redrawLine();
            this.reader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record))
            print(getFormatter().format(record), LevelColor.fromLevel(record.getLevel()).getColor());
    }

    @Override
    public void flush() {
        try {
            this.reader.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws SecurityException {}
}
