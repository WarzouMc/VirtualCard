package fr.warzou.virtualcard.utils.module.packets.packet;

import fr.warzou.virtualcard.utils.module.stream.ModuleStreamType;

/**
 * Input packet
 * @author Warzou
 * @version 0.0.2
 */
public interface PacketIn extends Packet {

    @Override
    default int packetType() {
        return ModuleStreamType.INPUT.ordinal();
    }

}
