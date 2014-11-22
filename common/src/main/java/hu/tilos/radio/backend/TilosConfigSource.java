package hu.tilos.radio.backend;

import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provide configuration.
 */
public class TilosConfigSource implements ConfigSource {

    private static final Logger LOG = LoggerFactory.getLogger(TilosConfigSource.class);

    private Map<String, String> properties = new HashMap<>();

    public TilosConfigSource() {
        File configFile = new File("tilos.properties");
        LOG.info("Reading configuration from " + configFile.getAbsolutePath());
        if (configFile.exists()) {
            Properties p = new Properties();
            try {

                p.load(new FileInputStream(configFile));
                for (String key : p.stringPropertyNames()) {
                    properties.put(key, p.getProperty(key));
                }
            } catch (IOException e) {
                LOG.error("Can't load file " + configFile.getAbsolutePath(), e);
            }
        }
    }

    @Override
    public int getOrdinal() {
        return 500;
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public String getPropertyValue(String s) {
        return getProperties().get(s);
    }

    @Override
    public String getConfigName() {
        return "tilos config";
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
