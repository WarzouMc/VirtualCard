package fr.warzou.virtualcard.api.events.events;

import fr.warzou.virtualcard.utils.event.Event;
import fr.warzou.virtualcard.utils.module.packets.packet.Packet;

public abstract class PacketEvent<P extends Packet> extends Event {

    private final P packet;

    public PacketEvent(P packet) {
        this.packet = packet;
    }

    public P getPacket() {
        return this.packet;
    }
}
