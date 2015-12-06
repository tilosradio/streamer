package hu.radio.tilos.model.type;

public enum MixCategory implements DescriptiveType {
    DJ("Tilos DJ"),
    GUESTDJ("Vendég DJ"),
    SHOW("Beszélgetős műsor"),
    TALE("Tilos mese");

    private String description;

    MixCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
