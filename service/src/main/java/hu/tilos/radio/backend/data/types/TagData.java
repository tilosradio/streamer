package hu.tilos.radio.backend.data.types;

import hu.radio.tilos.model.type.TagType;

public class TagData {

    private String name;

    private TagType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TagType getType() {
        return type;
    }

    public void setType(TagType type) {
        this.type = type;
    }
}
