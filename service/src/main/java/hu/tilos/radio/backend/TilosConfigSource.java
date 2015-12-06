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

    private Map<String, String> envAliases = new HashMap<>();

    public TilosConfigSource() {

        envAliases.put("MONGO_HOST", "MONGODB_PORT_28017_TCP_ADDR");

        File configFile = new File("tilos.properties");
        LOG.info("Reading the configuration from " + configFile.getAbsolutePath());
        for (String key : System.getenv().keySet()) {
            LOG.debug("ENV: " + key + " " + System.getenv(key));
        }
        if (configFile.exists()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(configFile));
                for (String key : p.stringPropertyNames()) {
                    String envName = propertyToEnvName(key);
                    if (System.getenv().containsKey(envName)) {
                        properties.put(key, System.getenv(envName));
                    } else if (envAliases.containsKey(envName) && System.getenv().containsKey(envAliases.get(envName))) {
                        properties.put(key, System.getenv(envAliases.get(envName)));
                    } else {
                        properties.put(key, p.getProperty(key));
                    }
                    LOG.debug("PROP: " + key + "=" + properties.get(key));
                }
            } catch (IOException e) {
                LOG.error("Can't load file " + configFile.getAbsolutePath(), e);
            }
        }
    }

    public static String propertyToEnvName(String propertyName) {
        return propertyName.replace('.', '_').toUpperCase();
    }

    public String getConfiguration(String key) {
        return properties.get(key);
    }
}
