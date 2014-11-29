package hu.tilos.radio.backend.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.bson.types.ObjectId;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;

public class ResolvedReferenceDecoder implements CustomConverter {

    private DB db;

    private DozerBeanMapper mapper;

    public ResolvedReferenceDecoder(DB db, DozerBeanMapper mapper) {
        this.db = db;
        this.mapper = mapper;
    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        DBRef ref = (DBRef) sourceFieldValue;
        if (sourceFieldValue != null) {
            DBObject childMongoObject = db.getCollection(ref.getRef()).findOne(new BasicDBObject("_id", new ObjectId(ref.getId().toString())));
            return mapper.map(childMongoObject, destinationClass);
        } else {
            return null;
        }
    }
}
