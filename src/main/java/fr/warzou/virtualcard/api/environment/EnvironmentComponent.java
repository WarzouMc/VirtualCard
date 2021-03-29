package fr.warzou.virtualcard.api.environment;

import fr.warzou.virtualcard.api.environment.path.PropertyMap;

public interface EnvironmentComponent {

    String name();

    PropertyMap getProperties();

}
