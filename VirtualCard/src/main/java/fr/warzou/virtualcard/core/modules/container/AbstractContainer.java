package fr.warzou.virtualcard.core.modules.container;

import fr.warzou.virtualcard.api.environment.EnvironmentComponent;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.container.conveyor.Conveyor;
import fr.warzou.virtualcard.core.modules.link.Linkable;
import fr.warzou.virtualcard.core.modules.link.LinkedModule;
import fr.warzou.virtualcard.core.modules.link.Point;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.stream.ModuleIOStream;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractContainer extends EnvironmentComponent<ModuleIOStream> implements Linkable {

    public abstract boolean inject(Item<?> item);

    public abstract boolean eject(Item<?> item);

    public abstract List<Item<?>> content();

    @Override
    public Optional<LinkedModule> linkedModuleFromModule(@NotNull ModuleBase<?> moduleBase) {
        LinkedModule[] linkedModules = links();
        if (linkedModules == null || linkedModules.length == 0)
            return Optional.empty();

        for (LinkedModule linkedModule : linkedModules) {
            if (linkedModule.getModuleBase().equals(moduleBase))
                return Optional.of(linkedModule);
        }
        return Optional.empty();
    }

    @Override
    public Optional<LinkedModule> linkedModuleFromPoint(@NotNull Point point) {
        LinkedModule[] linkedModules = links();
        if (linkedModules == null || linkedModules.length == 0)
            return Optional.empty();

        for (LinkedModule linkedModule : linkedModules) {
            if (linkedModule.getPoint().equals(point))
                return Optional.of(linkedModule);
        }
        return Optional.empty();
    }
}
