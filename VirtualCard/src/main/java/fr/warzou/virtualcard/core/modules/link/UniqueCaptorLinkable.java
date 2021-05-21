package fr.warzou.virtualcard.core.modules.link;

import fr.warzou.virtualcard.core.modules.container.conveyor.Conveyor;

/**
 * <p>This interface is use on the module {@link Conveyor}, but it could be use on every every module.</p>
 * <p>That simplify the captor system with the possibility to cast a {@link fr.warzou.virtualcard.utils.module.ModuleBase} into a {@link UniqueCaptorLinkable}</p>
 * @author Warzou
 */
public interface UniqueCaptorLinkable extends Linkable {

    /**
     * Return the unique {@link fr.warzou.virtualcard.core.modules.captor.Captor} who is linked on the module.
     * @return {@link LinkedModule} with {@link fr.warzou.virtualcard.core.modules.captor.Captor} in {@link fr.warzou.virtualcard.utils.module.ModuleBase}
     */
    LinkedModule getCaptor();

    /**
     * <p>Allow to obtain the position of the {@link fr.warzou.virtualcard.core.modules.captor.Captor} on the module</p>
     * <p>That is use to simplify the capt system</p>
     * @return Position of the {@link fr.warzou.virtualcard.core.modules.captor.Captor} on the module
     */
    int captQueue();

    /**
     * This methods return a {@link fr.warzou.virtualcard.core.modules.container.conveyor.Conveyor.ConveyorItem} array
     * cause this object contain the methode {@link Conveyor.ConveyorItem#getPosition()}
     * @return Return every element in the {@link fr.warzou.virtualcard.utils.module.ModuleBase}
     */
    Conveyor.ConveyorItem<?>[] read();

}
