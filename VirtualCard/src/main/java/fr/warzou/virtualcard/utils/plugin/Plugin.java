package fr.warzou.virtualcard.utils.plugin;

import fr.warzou.virtualcard.api.Card;

/**
 * Just plugin interface
 * <p>See implementation {@link fr.warzou.virtualcard.api.core.plugin.CardPlugin}</p>
 * <p>plugin.json format : </p>
 * <pre>
 *     {
 *         "name": "plugin name", # must be present
 *         "version": "plugin version", # must be present
 *         "authors": ["author1", "author2", ...], # must be present
 *         "main": "main class path", # must be present
 *         "commands": {
 *             "commandname": {
 *                  "description": "command description", # must be present
 *                  "help": "command help message", # not required
 *                  "alias": ["alias1", "alias2", ...] # not required
 *             },
 *             ...
 *         }, # not required if plugin doesn't add commands
 *         "modules": {
 *              "modulename": "module directory"
 *              ...
 *         } # bit required if plugin doesn't implement new module
 *     }
 * </pre>
 * @author Warzou
 * @version 0.0.2
 */
public interface Plugin {

    /**
     * This method return the {@link PluginInformation} of this {@link Plugin}
     * @return plugin information
     */
    PluginInformation getInformation();

    /**
     * Obtain what Card has call the execution of this plugin.
     * @return card who execute the plugin
     */
    Card getCard();
}
