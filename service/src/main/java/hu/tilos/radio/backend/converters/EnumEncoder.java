package hu.tilos.radio.backend.converters;

import org.dozer.CustomConverter;

/**
 * Converter to convert enum to integer.
 */
public class EnumEncoder implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        try {
            return (Integer) sourceClass.getMethod("ordinal", null).invoke(sourceFieldValue);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
