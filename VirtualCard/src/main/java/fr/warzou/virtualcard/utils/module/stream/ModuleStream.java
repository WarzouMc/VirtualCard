package fr.warzou.virtualcard.utils.module.stream;

import fr.warzou.virtualcard.utils.property.PropertyMap;
import org.jetbrains.annotations.Nullable;

/**
 * Juste the main interface for module stream.
 * <p>This has not for goal to be used in an other case than implementation in {@link ModuleInputStream} and {@link ModuleOutputStream}</p>
 * @author Warzou
 * @version 0.0.2
 */
public interface ModuleStream {

    /**
     * Returns {@link PropertyMap} with in key a packet name and un value a {@link fr.warzou.virtualcard.utils.module.packets.PacketPath}.
     * @return property map of packet
     */
    @Nullable
    PropertyMap getPacketMap();

}
