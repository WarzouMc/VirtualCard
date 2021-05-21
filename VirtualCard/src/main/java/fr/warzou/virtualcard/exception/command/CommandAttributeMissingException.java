package fr.warzou.virtualcard.exception.command;

public class CommandAttributeMissingException extends Exception {

    public CommandAttributeMissingException(String commandName, AttributeType attributeType) {
        super("'" + attributeType.getAttributeName() + "' : The following attribute is missing on command '" + commandName + "'");
    }
    
    public enum AttributeType {
        NAME("name"),
        DESCRIPTION("desciption");

        private final String attributeName;
        AttributeType(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getAttributeName() {
            return this.attributeName;
        }
    }

}
