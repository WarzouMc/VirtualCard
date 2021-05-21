package fr.warzou.virtualcard.utils.module.packets;

import fr.warzou.virtualcard.utils.module.ModuleBase;

/**
 * Just the destination of a packet.
 * @author Warzou
 * @version 0.0.2
 */
public class PacketDestination {

    /**
     * Targeting {@link ModuleBase}
     */
    private final ModuleBase<?> moduleBase;

    /**
     * Create a new instance of {@link PacketDestination}
     * @param moduleBase destination
     */
    public PacketDestination(ModuleBase<?> moduleBase) {
        this.moduleBase = moduleBase;
    }

    /**
     * That returns packet {@link ModuleBase} destination.
     * @return destination
     */
    public ModuleBase<?> getModuleBase() {
        return this.moduleBase;
    }

    @Override
    public String toString() {
        return "PacketDestination{" +
                "moduleBase=" + moduleBase +
                '}';
    }
}
