package fr.warzou.virtualcard.utils.module.packets.packet;

import fr.warzou.virtualcard.utils.module.stream.ModuleStreamType;

/**
 * Output packet.
 * @author Warzou
 * @version 0.0.2
 */
public interface PacketOut extends Packet {

    @Override
    default int packetType() {
        return ModuleStreamType.OUTPUT.ordinal();
    }

}
