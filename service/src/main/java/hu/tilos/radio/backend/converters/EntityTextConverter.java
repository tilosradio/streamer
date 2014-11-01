package hu.tilos.radio.backend.converters;

import hu.tilos.radio.backend.data.EntitySelector;
import org.dozer.ConfigurableCustomConverter;
import org.dozer.CustomConverter;
import org.dozer.DozerConverter;

import javax.persistence.EntityManager;

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
