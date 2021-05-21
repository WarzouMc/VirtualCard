package fr.warzou.virtualcard.utils.module.packets;

import fr.warzou.virtualcard.utils.module.stream.ModuleStream;

/**
 * Packet name characteristics view.
 * {@link PacketPath} could be be found in {@link ModuleStream#getPacketMap()}
 * @author Warzou
 * @version 0.0.2
 */
public class PacketPath {

    /**
     * Packet target file.
     */
    private final String file;
    /**
     * Packet key path in file
     */
    private final String path;
    /**
     * Packet end point
     */
    private final String end;

    /**
     * Create a new instance of {@link PacketPath}
     *
     * @param file packet file
     * @param path packet path in file
     * @param end packet end point
     */
    public PacketPath(String file, String path, String end) {
        this.file = file;
        this.path = path;
        this.end = end;
    }

    /**
     * Returns the file who content this packet.
     * @return packet file
     */
    public String asFile() {
        return this.file;
    }

    /**
     * Return packet key path in file.
     * @return packet path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Return the packet end point.
     * @return packet end point
     */
    public String getEnd() {
        return this.end;
    }

    @Override
    public String toString() {
        return "PacketPath{" +
                "file='" + file + '\'' +
                ", path='" + path + '\'' +
                ", end='" + end + '\'' +
                '}';
    }
}
