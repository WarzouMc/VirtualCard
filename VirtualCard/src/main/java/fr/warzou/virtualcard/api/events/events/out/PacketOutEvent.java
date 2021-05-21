package fr.warzou.virtualcard.api.events.events.out;

import fr.warzou.virtualcard.api.events.events.PacketEvent;
import fr.warzou.virtualcard.utils.module.packets.packet.PacketOut;

public class PacketOutEvent extends PacketEvent<PacketOut> {

    public PacketOutEvent(PacketOut packet) {
        super(packet);
    }
}
