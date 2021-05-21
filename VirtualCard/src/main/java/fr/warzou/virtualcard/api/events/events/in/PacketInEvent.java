package fr.warzou.virtualcard.api.events.events.in;

import fr.warzou.virtualcard.api.events.events.PacketEvent;
import fr.warzou.virtualcard.utils.module.packets.packet.PacketIn;

public class PacketInEvent extends PacketEvent<PacketIn> {

    public PacketInEvent(PacketIn packet) {
        super(packet);
    }
}
