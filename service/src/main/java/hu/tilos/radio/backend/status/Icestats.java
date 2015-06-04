package hu.tilos.radio.backend.status;

import java.util.List;

public class Icestats {
    private List<IceSource> source;

    public List<IceSource> getSource() {
        return source;
    }

    public void setSource(List<IceSource> source) {
        this.source = source;
    }
}
