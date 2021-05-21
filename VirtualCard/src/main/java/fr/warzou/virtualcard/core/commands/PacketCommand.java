package fr.warzou.virtualcard.core.commands;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.module.ModuleManager;
import fr.warzou.virtualcard.api.environment.CardEnvironment;
import fr.warzou.virtualcard.utils.property.PropertyMap;
import fr.warzou.virtualcard.utils.property.SinglePropertyEntry;
import fr.warzou.virtualcard.utils.command.command.executor.CommandExecutor;
import fr.warzou.virtualcard.utils.command.commandsystem.Command;
import fr.warzou.virtualcard.exception.packet.PacketNameException;
import fr.warzou.virtualcard.exception.packet.PacketNameInterpreterException;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.packets.PacketPath;
import fr.warzou.virtualcard.utils.module.packets.PacketSource;
import fr.warzou.virtualcard.utils.module.packets.packet.*;
import fr.warzou.virtualcard.utils.module.stream.ModuleStreamType;
import fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class PacketCommand implements CommandExecutor {

    private final Card card;

    public PacketCommand(Card card) {
        this.card = card;
    }

    @Override
    public boolean execute(Command command, String[] arguments) {
        if (!command.getName().equalsIgnoreCase("packet"))
            return false;

        if (arguments.length < 1)
            return false;

        if (arguments[0].equalsIgnoreCase("list"))
            return packetList();

        if (arguments[0].equalsIgnoreCase("out"))
            return out(arguments);
        if (arguments[0].equalsIgnoreCase("in"))
            return in(arguments);
        return false;
    }

    private boolean packetList() {
        ModuleManager moduleManager = this.card.getModuleManager();
        PropertyMap moduleMap = moduleManager.getModuleMap();
        List<ModuleBase<?>> moduleBases = new ArrayList<>();
        moduleMap.entries().filter(ModuleBase.class).forEach(entry -> moduleBases.add(entry.value().value()));
        for (ModuleBase<?> module : moduleBases) {
            PropertyMap packetMap = module.getStream().getPacketMap();
            if (packetMap == null) {
                new NullPointerException("Packet map couldn't be null").printStackTrace(System.err);
                return false;
            }
            System.out.println(Ansi.ansi().bg(Ansi.Color.YELLOW).toString() + "Packet from module '" +
                    module.moduleName() + "' :");
            int count = 0;
            for (SinglePropertyEntry<PacketPath> entry : packetMap.entries().filter(PacketPath.class)) {
                Ansi ansi = count % 2 == 0 ? Ansi.ansi().bg(Ansi.Color.CYAN) : Ansi.ansi().bg(Ansi.Color.BLUE);
                System.out.println(ansi.toString() + "Packet : " + entry.key() + " ; Path : " + entry.value().value());
                count++;
            }
        }
        return true;
    }

    public boolean out(String[] arguments) {
        CardEnvironment environment = this.card.getEnvironment();
        ModuleManager moduleManager = this.card.getModuleManager();
        if (arguments.length != 2)
            return false;

        String packetName = arguments[1];
        try {
            Packet underminedPacket = new PacketConstructor(moduleManager, packetName,
                    new PacketSource(getClass(), environment.systemClock().nowTicks())).getPacket();
            if (underminedPacket.packetType() != ModuleStreamType.OUTPUT.ordinal())
                return false;
            PacketOut packet = (PacketOut) underminedPacket;
            Reader<?> reader = PacketDispatcher.outDispatch(this.card, packet);
            Optional<?> optional = reader.read();
            if (!optional.isPresent())
                return false;
            System.out.println(optional.get());
            return true;
        } catch (PacketNameException | PacketNameInterpreterException | MissingPropertyException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean in(String[] arguments) {
        this.card.getLogger().log("In packet sender command is not implemented yet", Level.WARNING);
        return true;
    }
}
