package hu.tilos.radio.backend.spark;

import com.google.inject.MembersInjector;
import hu.tilos.radio.backend.Configuration;

import java.lang.reflect.Field;

public class ConfigurationInjector<T> implements MembersInjector<T> {
    private Field field;

    public ConfigurationInjector(Field field) {
        this.field = field;
        field.setAccessible(true);
    }


    @Override
    public void injectMembers(Object o) {
        try {
            if (field.getType().equals(String.class)) {
                String name = field.getAnnotation(Configuration.class).name();
                if (name.equals("mongo.db")) {
                    field.set(o, "tilos");
                } else if (name.equals("mongo.host")) {
                    field.set(o, "localhost");
                } else if (name.equals("jwt.secret")) {
                    field.set(o, "xxx");
                } else if (name.equals("upload.dir")) {
                    field.set(o, "/tmp");
                } else if (name.equals("recaptcha.privatekey")) {
                    field.set(o, "recaptcha.privatekey");
                } else if (name.equals("server.url")) {
                    field.set(o, "http://tilos.hu");

                } else {
                    throw new IllegalArgumentException(name);
                }

            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
