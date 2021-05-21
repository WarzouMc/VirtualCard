package fr.warzou.virtualcard.core.modules.link;

import java.util.Arrays;

/**
 * Define the size of all module face.
 * This was implemented for the {@link Linkable} interface to make easier every linkage checks and getters.
 * @author Warzou
 */
public interface Size {

    /**
     * This methode allow to get the different face size of this module, notably use for {@link LinkedModule}.
     * @return Size for all face in order ({@link fr.warzou.virtualcard.core.modules.link.LinkedModule.Face#LEFT},
     * {@link fr.warzou.virtualcard.core.modules.link.LinkedModule.Face#RIGHT}, {@link fr.warzou.virtualcard.core.modules.link.LinkedModule.Face#FRONT},
     * {@link fr.warzou.virtualcard.core.modules.link.LinkedModule.Face#BEHIND}, {@link fr.warzou.virtualcard.core.modules.link.LinkedModule.Face#UP},
     * {@link fr.warzou.virtualcard.core.modules.link.LinkedModule.Face#DOWN})
     */
    int[] size();

    /**
     * This method return every point who is allow the module linkage, notably use for {@link LinkedModule}
     * @return Every point could support module linkage.
     */
    Point[] linkablePoint();

    /**
     * Check if a {@link Point} is linkable.
     * @param point Target {@link Point} to be checked
     * @return true if {@link Size#linkablePoint()} contain the target point, and false otherwise
     */
    default boolean isLinkable(Point point) {
        return Arrays.asList(linkablePoint()).contains(point);
    }
}
