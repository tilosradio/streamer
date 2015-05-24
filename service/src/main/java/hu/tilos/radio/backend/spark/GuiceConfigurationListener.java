package hu.tilos.radio.backend.spark;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import hu.tilos.radio.backend.Configuration;

import java.lang.reflect.Field;

public class GuiceConfigurationListener implements TypeListener, ProvisionListener {

    @Override
    public <T> void onProvision(ProvisionInvocation<T> provisionInvocation) {

    }

    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
        Class<?> clazz = typeLiteral.getRawType();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Configuration.class)) {
                    typeEncounter.register(new ConfigurationInjector<I>(field));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
