package fr.warzou.virtualcard.utils.module.packets;

/**
 * Traced the packet route.
 * @author Warzou
 * @version 0.0.2
 */
public class PacketTrace {

    /**
     * Source
     */
    private final PacketSource source;
    /**
     * Destination
     */
    private final PacketDestination destination;

    /**
     * Create a new instance of {@link PacketTrace}.
     * @param source packet source
     * @param destination packet destination
     */
    public PacketTrace(PacketSource source, PacketDestination destination) {
        this.source = source;
        this.destination = destination;
    }

    /**
     * Returns {@link PacketSource}
     * @return packet source
     */
    public PacketSource getSource() {
        return this.source;
    }

    /**
     * Return {@link PacketDestination}
     * @return packet destination
     */
    public PacketDestination getDestination() {
        return this.destination;
    }

    @Override
    public String toString() {
        return "PacketTrace{" +
                "source=" + source +
                ", destination=" + destination +
                '}';
    }
}
