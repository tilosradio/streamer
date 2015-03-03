package hu.tilos.radio.backend.converters;

import org.dozer.CustomConverter;

/**
 * Converter to prefix string value.
 */
public class PrefixingConverter implements CustomConverter {

    private String prefix;

    private String nonStart;

    public PrefixingConverter(String prefix, String nonStart) {
        this.prefix = prefix;
        this.nonStart = nonStart;
    }

    public PrefixingConverter(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNonStart() {
        return nonStart;
    }

    public void setNonStart(String nonStart) {
        this.nonStart = nonStart;
    }



    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        String source = (String) sourceFieldValue;
        if (source == null || (nonStart != null && source.startsWith(nonStart))) {
            return source;
        } else {
            return prefix + source;
        }

    }
}
