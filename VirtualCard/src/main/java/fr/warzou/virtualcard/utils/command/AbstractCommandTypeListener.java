package fr.warzou.virtualcard.utils.command;

/**
 * Default methods for a command listener.
 * <p>Not really useful.</p>
 * @author Warzou
 * @version 0.0.2
 */
public abstract class AbstractCommandTypeListener {

    /**
     * Listen method
     */
    protected abstract void listen();

    /**
     * Shutdown listener
     */
    protected abstract void shutdown();

}
