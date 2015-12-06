package hu.tilos.radio.backend.spark;

import com.google.inject.MembersInjector;
import com.typesafe.config.Config;
import hu.tilos.radio.backend.Configuration;

import java.lang.reflect.Field;

public class ConfigurationInjector<T> implements MembersInjector<T> {


    private Field field;

    private Config config;

    public ConfigurationInjector(Field field, Config config) {
        this.field = field;
        field.setAccessible(true);
        this.config = config;
    }


    @Override
    public void injectMembers(Object o) {
        try {
            if (field.getType().equals(String.class)) {
                String name = field.getAnnotation(Configuration.class).name();
                String configValue = config.getString(name);
                if (configValue == null || configValue.trim().length() == 0) {
                    throw new IllegalArgumentException("Undefined configuration paramteter " + name);
                }
                field.set(o, configValue);
            } else if (field.getType().equals(boolean.class)) {
                String name = field.getAnnotation(Configuration.class).name();
                boolean configValue = config.getBoolean(name);
                field.set(o, configValue);
            } else {
                throw new IllegalArgumentException("Config type is not supported " + field.getType());
            }
        } catch (
                IllegalAccessException e
                )

        {
            throw new RuntimeException(e);
        }
    }
}
