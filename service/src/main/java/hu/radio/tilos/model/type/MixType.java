package hu.radio.tilos.model.type;

public enum MixType implements DescriptiveType {

    MUSIC("Zene"),
    SPEECH("Beszéd");

    private final String description;

    MixType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
