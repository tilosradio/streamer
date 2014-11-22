package hu.tilos.radio.backend;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

public class ConfigurationProducer {

    @Inject
    TilosConfigSource configSource;

    @Produces
    @Configuration(name = "")
    public String injectConfiguration(InjectionPoint ip) {
        Configuration annotation = ip.getAnnotated().getAnnotation(Configuration.class);
        return configSource.getConfiguration(annotation.name());
    }
}
