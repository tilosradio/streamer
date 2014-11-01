package hu.tilos.radio.backend;

import org.apache.deltaspike.core.api.config.ConfigProperty;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.Properties;

public class TestConfigProvider {
    private Properties properties;

    public TestConfigProvider() {
        properties = new Properties();
        properties.put("server.url", "http://tilos.hu");
        properties.put("jwt.secret", "veryeasy");
    }

    @ConfigProperty(name = "*")
    @Produces
    public String getConfigValue(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        String name = annotated.getAnnotation(ConfigProperty.class).name();
        return properties.getProperty(name);
    }


}
