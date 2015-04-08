package hu.tilos.radio.backend.converters;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;

import java.util.List;

public class MongoListEncoder implements CustomConverter {

    DozerBeanMapper mapper;

    public MongoListEncoder(DozerBeanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        if (sourceFieldValue == null) {
            return null;
        }
        List sourceList = (List) sourceFieldValue;
        BasicDBList newObject = new BasicDBList();
        for (Object element : sourceList) {
            BasicDBObject object = new BasicDBObject();
            mapper.map(element, object);
            newObject.add(object);
        }
        return newObject;
    }
}
