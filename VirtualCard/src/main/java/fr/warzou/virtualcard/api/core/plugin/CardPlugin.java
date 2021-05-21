package fr.warzou.virtualcard.api.core.plugin;

import fr.warzou.virtualcard.api.Card;
import fr.warzou.virtualcard.api.starter.CardClassLoader;
import fr.warzou.virtualcard.utils.plugin.Plugin;
import fr.warzou.virtualcard.utils.plugin.PluginInformation;

public class CardPlugin implements Plugin {

    private Card card;
    private PluginInformation pluginInformation;

    public CardPlugin() {
        ClassLoader loader = getClass().getClassLoader();
        if (!(loader instanceof CardClassLoader))
            throw new IllegalStateException("Invalid class loader");
    }

    public static CardPlugin init(Card card, Class<? extends CardPlugin> clazz, PluginInformation information) {
        CardPlugin plugin;
        try {
            plugin = clazz.newInstance();
            plugin.card = card;
            plugin.pluginInformation = information;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        if (!card.getPluginsManager().pluginRegister.register(plugin))
            return null;
        return plugin;
    }

    public void onStart() {}

    public void onStop() {}

    @Override
    public PluginInformation getInformation() {
        return this.pluginInformation;
    }

    public Card getCard() {
        return this.card;
    }
}
