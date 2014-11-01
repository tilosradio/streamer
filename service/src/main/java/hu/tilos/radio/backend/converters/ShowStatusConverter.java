package hu.tilos.radio.backend.converters;

import org.apache.commons.beanutils.Converter;

public class ShowStatusConverter implements Converter {
    @Override
    public <T> T convert(Class<T> tClass, Object o) {
        return (T) "asd";
    }
}
