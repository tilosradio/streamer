package hu.tilos.radio.backend;

import javax.enterprise.inject.Alternative;
import java.util.Properties;

@Alternative
public class TestConfigProvider extends TilosConfigSource {

    private Properties properties;

    public TestConfigProvider() {
        properties = new Properties();
        properties.put("server.url", "http://tilos.hu");
        properties.put("jwt.secret", "veryeasy");
        properties.put("influxdb.url", "localhost");
    }

    @Override
    public String getConfiguration(String key) {
        return (String) properties.get(key);
    }
}



