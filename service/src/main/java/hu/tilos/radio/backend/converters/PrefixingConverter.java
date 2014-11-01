package hu.tilos.radio.backend.converters;

import org.modelmapper.AbstractConverter;

/**
 * Converter to prefix string value.
 */
public class PrefixingConverter extends AbstractConverter<String, String> {

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
    protected String convert(String source) {
        if (source == null || (nonStart != null && source.startsWith(nonStart))) {
            return source;
        } else {
            return prefix + source;
        }
    }
}
