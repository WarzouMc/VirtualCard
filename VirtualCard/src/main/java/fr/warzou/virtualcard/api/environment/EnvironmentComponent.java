package fr.warzou.virtualcard.api.environment;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.utils.event.EventsManager;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.stream.ModuleStream;
import org.jetbrains.annotations.Nullable;

public abstract class EnvironmentComponent<S extends ModuleStream> implements ModuleBase<S> {

    protected Card card;
    protected CardEnvironment environment;
    protected EventsManager eventsManager;

    @Nullable
    public EnvironmentComponent<S> init(Card card) {
        if (this.environment != null)
            return null;
        this.card = card;
        this.environment = this.card.getEnvironment();
        this.eventsManager = this.card.getEventManager();

        this.card.getModuleManager().addModule(this);
        return this;
    }
}
