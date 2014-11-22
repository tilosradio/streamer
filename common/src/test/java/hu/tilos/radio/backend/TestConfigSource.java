package hu.tilos.radio.backend;

import javax.enterprise.inject.Alternative;

@Alternative
public class TestConfigSource extends TilosConfigSource {


    @Override
    public String getConfiguration(String key) {
        return "asd";
    }
}
