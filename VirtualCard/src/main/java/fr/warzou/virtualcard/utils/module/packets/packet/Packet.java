package fr.warzou.virtualcard.utils.module.packets.packet;

import fr.warzou.virtualcard.utils.module.packets.PacketPath;
import fr.warzou.virtualcard.utils.module.packets.PacketTrace;

/**
 * Just a packet.
 * <p>Packet name style :</p>
 * <p>modulename.[in/out].property</p>
 * @author Warzou
 * @version 0.0.2
 */
public interface Packet {

    /**
     * Return packet stream type.
     * <p>Ordinal of {@link fr.warzou.virtualcard.utils.module.stream.ModuleStreamType#INPUT} or {@link fr.warzou.virtualcard.utils.module.stream.ModuleStreamType#OUTPUT}</p>
     * @return packet stream type
     */
    int packetType();

    /**
     * Returns path of this packet
     * @return packet path
     */
    PacketPath packetPath();

    /**
     * Returns endpoint of this packet
     * @return endpoint
     */
    String endpoint();

    /**
     * Returns name of this packet
     * @return packet name
     */
    String packetName();

    /**
     * Returns target property for this packet
     * @return packet property
     */
    String property();

    /**
     * Returns trace of this packet
     * @return packet trace
     */
    PacketTrace trace();

}
