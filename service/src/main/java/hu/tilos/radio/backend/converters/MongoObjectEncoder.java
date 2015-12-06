package hu.tilos.radio.backend.converters;

import com.mongodb.BasicDBObject;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;

public class MongoObjectEncoder implements CustomConverter {

    DozerBeanMapper mapper;

    public MongoObjectEncoder(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        if (sourceFieldValue == null) {
            return null;
        }
        BasicDBObject newObject = new BasicDBObject();
        mapper.map(sourceFieldValue, newObject);
        return newObject;
    }
}
