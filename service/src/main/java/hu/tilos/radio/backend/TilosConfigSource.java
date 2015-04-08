package hu.tilos.radio.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provide configuration.
 */
@Singleton
public class TilosConfigSource {

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

    public String getConfiguration(String key) {
        return properties.get(key);
    }
}
