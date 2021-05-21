package fr.warzou.virtualcard.utils.property;

/**
 * This interface represent a value in {@link PropertyMap}
 * <p>This interface simplify the management of the value in {@link PropertyMap}</p>
 * <p>Property could be get with {@link PropertyMap#getProperty(String)}, {@link PropertyMap#getProperty(String, Class)} or {@link SinglePropertyEntry#value()}</p>
 * @see fr.warzou.virtualcard.api.core.propertyimpl.PropertyImpl implementation
 * @version 0.0.2
 * @author Warzou
 * @param <T> Value class type
 */
public interface Property<T> {

    /**
     * This methode provides current property key.
     * @return this property key
     */
    String key();

    /**
     * This methode provides current property value.
     * @return this property value
     */
    T value();

}
