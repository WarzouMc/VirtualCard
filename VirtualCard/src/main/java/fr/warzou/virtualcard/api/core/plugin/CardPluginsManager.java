package fr.warzou.virtualcard.api.core.plugin;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.core.propertyimpl.AbstractLockablePropertyMap;
import fr.warzou.virtualcard.exception.property.MissingPropertyException;
import fr.warzou.virtualcard.utils.plugin.Plugin;
import fr.warzou.virtualcard.utils.plugin.PluginInformation;

import java.util.Set;

public class CardPluginsManager {

    private final Card card;
    protected final PluginRegister pluginRegister;

    public CardPluginsManager(Card card) {
        this.card = card;
        this.pluginRegister = new PluginRegister(this.card);
    }

    public void finish() {
        if (((AbstractLockablePropertyMap) this.pluginRegister.getPropertyMap()).isLock())
            return;
        ((AbstractLockablePropertyMap) this.pluginRegister.getPropertyMap()).lock();
    }

    public Set<String> plugins() {
        return this.pluginRegister.getPropertyMap().keys();
    }

    public PluginRegister getPluginRegister() {
        return this.pluginRegister;
    }

    public PluginInformation getPluginInformation(String pluginName) {
        if (!this.pluginRegister.getPropertyMap().containKey(pluginName))
            return null;
        try {
            Plugin plugin = this.pluginRegister.getPropertyMap().getProperty(pluginName, Plugin.class).value();
            return plugin.getInformation();
        } catch (MissingPropertyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
