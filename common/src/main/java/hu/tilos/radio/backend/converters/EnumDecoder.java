package hu.tilos.radio.backend.converters;

import org.dozer.CustomConverter;

/**
 * Converter to convert enum to integer.
 */
public class EnumDecoder implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        try {
            Object[] values = (Object[]) destinationClass.getMethod("values", null).invoke(null);
            return values[(int) sourceFieldValue];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
