package fr.warzou.virtualcard.api.core.logger;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.ticktask.CardTick;
import fr.warzou.virtualcard.core.modules.CardClock;
import fr.warzou.virtualcard.utils.task.impl.ExecutorThreadPool;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class CardLogger extends Logger {

    private boolean loggable = true;
    private final String name;
    private final ConsoleReader consoleReader;
    private Card card;

    private File latestLogFile;
    private String path;

    private final LogDispatcher dispatcher = new LogDispatcher(this);

    public CardLogger(String name, ConsoleReader consoleReader) {
        super(name, null);
        this.name = name;
        this.consoleReader = consoleReader;
    }

    public void initLogFiles(File file, long date) {
        if (!file.isDirectory())
            return;
        System.out.println("Logger date format : '" + "yyyy-MM-dd-HH-mm-ss" + "'");
        String dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date);
        System.out.println("Create log file");
        String textFileName = "log-" + dateFormat;
        this.path = "logs" + File.separator + textFileName;
        this.latestLogFile = new File("logs" + File.separatorChar + "latest.txt");
        try {
            this.latestLogFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileHandler fileHandler = new FileHandler(this.latestLogFile.getPath());
            fileHandler.setFormatter(new LoggerFormatter(this.name));
            addHandler(fileHandler);
            Files.delete(new File(this.latestLogFile.getPath() + ".lck").toPath());

            ConsoleWriterHandler consoleWriterHandler = new ConsoleWriterHandler(this.consoleReader);
            consoleWriterHandler.setFormatter(new LoggerFormatter(this.name));
            addHandler(consoleWriterHandler);

            System.out.println(Ansi.ansi().bg(Ansi.Color.GREEN).toString() + "Start log dispatcher !");
            this.dispatcher.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CardLogger setCard(@NotNull Card card) {
        if (this.card != null)
            return this;
        this.card = card;
        return this;
    }

    public void log(Object object) {
        log(object.toString());
    }

    public void log(String message) {
        log(message, Level.INFO);
    }

    public void log(String message, Level level) {
        log(new LogRecord(level, message));
    }

    public void log(LogRecord logRecord) {
        if (!this.loggable || this.card == null)
            return;
        String message = logRecord.getMessage();
        if (message.endsWith("\n"))
            message = message.substring(0, message.length() - 1);
        logRecord.setMessage(message);
        this.dispatcher.put(logRecord);
    }

    public void logError(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        log(new LogRecord(Level.SEVERE, stringWriter.toString()));
    }

    public void close() {
        log(new LogRecord(Level.OFF, getName() + " logger is now closed"));
        Handler[] handlers = getHandlers().clone();
        this.dispatcher.getQueue().forEach(this::doLog);
        Arrays.asList(handlers).forEach(this::removeHandler);
        this.dispatcher.interrupt();
        this.loggable = false;
        packLog();
        CardClock.sleep(20);
    }

    private void packLog() {
        File file = new File(this.path + ".zip");
        File txtFile = new File(this.path + ".txt");
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
            zipOutputStream.putNextEntry(new ZipEntry(txtFile.getName()));
            byte[] bytes = Files.readAllBytes(Paths.get(this.latestLogFile.getPath()));
            zipOutputStream.write(bytes);
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doLog(LogRecord logRecord) {
        super.log(logRecord);
    }

    public static class LoggerFormatter extends Formatter {

        private final String name;

        public LoggerFormatter(String name) {
            this.name = name;
        }

        @Override
        public String format(LogRecord record) {
            return "[" +
                    new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) +
                    " " + name +
                    "] " +
                    "[" +
                    record.getLevel() +
                    "] " +
                    record.getMessage() +
                    "\n";
        }

        public String format(String message, CardClock cardClock) {
            return "[" +
                    new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(cardClock.nowTicks() * 1000 * CardTick.TICK) +
                    " " + name +
                    "] " +
                    message;
        }
    }
}
