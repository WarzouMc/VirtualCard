package fr.warzou.virtualcard.utils.module.packets;

/**
 * Packet source information.
 * @author Warzou
 * @version 0.0.2
 */
public class PacketSource {

    /**
     * Source class
     */
    private final Class<?> source;
    /**
     * Moment when the packet was send, in tick.
     */
    private final long when;

    /**
     * Create a new instance of {@link PacketSource}
     * @param source class source
     * @param when tick when the packet was send
     */
    public PacketSource(Class<?> source, long when) {
        this.source = source;
        this.when = when;
    }

    /**
     * Return the class where packet was call.
     * @return source class
     */
    public Class<?> getSource() {
        return this.source;
    }

    /**
     * Return when the packet was then.
     * @return when packet was send
     */
    public long getWhen() {
        return this.when;
    }

    @Override
    public String toString() {
        return "PacketSource{" +
                "source=" + source +
                ", when=" + when +
                '}';
    }
}
