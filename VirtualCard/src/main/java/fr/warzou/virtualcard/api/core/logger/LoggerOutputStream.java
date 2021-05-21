package fr.warzou.virtualcard.api.core.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerOutputStream extends ByteArrayOutputStream {

    private final Logger logger;
    private final Level level;

    public LoggerOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override
    public void flush() throws IOException {
        String string = toString(StandardCharsets.UTF_8.name());
        reset();
        if (string.isEmpty() || string.equals(System.getProperty("line.separator")))
            return;
        this.logger.logp(level, "", "", string);
    }
}
