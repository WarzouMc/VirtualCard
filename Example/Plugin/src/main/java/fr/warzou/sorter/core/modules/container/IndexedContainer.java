package fr.warzou.sorter.core.modules.container;

import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.container.container.Container;
import fr.warzou.virtualcard.core.modules.link.LinkedModule;
import fr.warzou.virtualcard.core.modules.link.Point;
import org.jetbrains.annotations.NotNull;

public class IndexedContainer extends Container.AbstractIndexedContainer {

    public IndexedContainer(@NotNull String sub, @NotNull String path) {
        super(sub, path);
    }

    @Override
    public boolean inject(Item<?> item) {
        return add(item);
    }

    @Override
    public boolean eject(Item<?> item) {
        return false;
    }

    @Override
    public LinkedModule[] links() {
        return new LinkedModule[0];
    }

    @Override
    public int[] size() {
        return new int[] {0, 0, 0, 0, 0, 0};
    }

    @Override
    public Point[] linkablePoint() {
        return new Point[0];
    }
}
