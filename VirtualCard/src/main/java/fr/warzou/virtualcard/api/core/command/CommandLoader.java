package fr.warzou.virtualcard.api.core.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;
import fr.warzou.virtualcard.exception.command.CommandAttributeMissingException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CommandLoader {

    private final Card card;

    public CommandLoader(@NotNull Card card) {
        this.card = card;
    }

    public void load(@NotNull InputStream stream) {
        JsonElement jsonElement;
        jsonElement = JsonParser.parseReader(new InputStreamReader(stream));

        JsonObject mainObject = jsonElement.getAsJsonObject();
        JsonArray commandList = mainObject.getAsJsonArray("commands");
        commandList.forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            CommandBuilder commandBuilder = new CommandBuilder(object);
            this.card.getCommandRegister().addCommand(commandBuilder.build());
        });
    }

    private static class CommandBuilder {

        private final JsonObject command;

        private String name;
        private String description;
        private String help;
        private String[] alias;

        CommandBuilder(JsonObject command) {
            this.command = command;
            try {
                init();
            } catch (CommandAttributeMissingException e) {
                e.printStackTrace();
            }
        }

        private void init() throws CommandAttributeMissingException {
            initName();
            initDescription();
            initHelp();
            initAlias();
        }

        private void initName() throws CommandAttributeMissingException {
            if (!this.command.has("name"))
                throw new CommandAttributeMissingException(null, CommandAttributeMissingException.AttributeType.NAME);
            this.name = this.command.get("name").getAsString();
        }

        private void initDescription() throws CommandAttributeMissingException {
            if (!this.command.has("description"))
                throw new CommandAttributeMissingException(this.name, CommandAttributeMissingException.AttributeType.DESCRIPTION);
            this.description = this.command.get("description").getAsString();
        }

        private void initHelp() {
            this.help = this.command.get("help").getAsString();
        }

        private void initAlias() {
            if (!this.command.has("alias"))
                this.alias = new String[0];
            else {
                JsonArray aliasArray = this.command.getAsJsonArray("alias");
                Iterator<JsonElement> iterator = aliasArray.iterator();
                List<String> aliasList = new ArrayList<>();
                while (iterator.hasNext()) {
                    JsonElement currentElement = iterator.next();
                    String aliasName = currentElement.getAsString();
                    aliasList.add(aliasName.toUpperCase());
                }
                Collections.sort(aliasList);
                this.alias = aliasList.toArray(new String[aliasArray.size()]);
            }
        }

        private Command build() {
            return new Command() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getDescription() {
                    return description;
                }

                @Override
                public String getHelpMessage() {
                    return help;
                }

                @Override
                public String[] getAlias() {
                    return alias;
                }

                @Override
                public String source() {
                    return "Card";
                }
            };
        }
    }

}
