package hu.radio.tilos.model.type;

public enum TagType implements DescriptiveType {

    GENERIC("Cimke"),
    PERSON("Ember");

    private final String description;

    TagType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
