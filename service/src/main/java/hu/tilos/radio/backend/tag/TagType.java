package hu.tilos.radio.backend.tag;

import hu.radio.tilos.model.type.DescriptiveType;

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
