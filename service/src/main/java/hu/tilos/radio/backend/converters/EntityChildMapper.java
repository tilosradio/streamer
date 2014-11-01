package hu.tilos.radio.backend.converters;

import hu.tilos.radio.backend.data.types.WithId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class EntityChildMapper implements Converter<WithId, Object> {

    @Inject
    EntityManager entityManager;

    @Override
    public Object convert(MappingContext<WithId, Object> context) {
        if (context.getSource()==null){
            return null;
        }
        return entityManager.find(context.getDestinationType(), context.getSource().getId());
    }
}
