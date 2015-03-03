package hu.tilos.radio.backend.converters;

import org.dozer.DozerConverter;

public class EntityTextConverter extends DozerConverter<Enum, String> {

    public static final String ID = "entityText";

    public EntityTextConverter() {
        super(Enum.class, String.class);
    }

    @Override
    public String convertTo(Enum source, String destination) {
        return getParameter().split(",")[source.ordinal()];
    }

    @Override
    public Enum convertFrom(String source, Enum destination) {
        throw new UnsupportedOperationException();
    }
}
