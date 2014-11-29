package hu.tilos.radio.backend.data.types;

import hu.radio.tilos.model.type.ShowStatus;
import hu.radio.tilos.model.type.ShowType;

public class ShowSimple {

    public String id;

    public String name;

    public String alias;

    public ShowType type;

    public ShowStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ShowType getType() {
        return type;
    }

    public void setType(ShowType type) {
        this.type = type;
    }

    public ShowStatus getStatus() {
        return status;
    }

    public void setStatus(ShowStatus status) {
        this.status = status;
    }
}
