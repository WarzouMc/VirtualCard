package fr.warzou.virtualcard.core.modules.link;

import fr.warzou.virtualcard.utils.module.ModuleBase;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Linkable extends Size {

    LinkedModule[] links();

    Optional<LinkedModule> linkedModuleFromModule(@NotNull ModuleBase<?> moduleBase);

    Optional<LinkedModule> linkedModuleFromPoint(@NotNull Point point);
}
