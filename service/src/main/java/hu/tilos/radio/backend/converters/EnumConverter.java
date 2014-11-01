package hu.tilos.radio.backend.converters;

import org.modelmapper.AbstractConverter;

/**
 * Converter to convert enum to integer.
 */
public class EnumConverter extends AbstractConverter<Enum, Integer> {
    @Override
    protected Integer convert(Enum source) {
        int a = 1;
        return source.ordinal();

    }
}
