package hu.tilos.radio.backend.converters;

import com.mongodb.DBRef;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;

public class ReferenceDecoder implements CustomConverter {

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        DBRef ref = (DBRef) sourceFieldValue;
        if (sourceFieldValue != null) {
            return ref.getId();
        } else {
            return null;
        }
    }
}
