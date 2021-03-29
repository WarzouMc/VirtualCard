package fr.warzou.virtualcard.utils.exception.property;

import fr.warzou.virtualcard.api.environment.EnvironmentComponent;

public class MissingPropertyException extends Exception {

    public MissingPropertyException(String property) {
        super("Couldn't find '" + property + "' property !");
    }

    public MissingPropertyException(EnvironmentComponent environmentComponent, String property) {
        super("Couldn't find '" + property + "' property in '" + environmentComponent.name() + "' environment !");
    }

    public MissingPropertyException(String property, Class<?> wrongClass) {
        super("'" + property + "' property is not a '" + wrongClass.getName() + "' !");
    }

}
