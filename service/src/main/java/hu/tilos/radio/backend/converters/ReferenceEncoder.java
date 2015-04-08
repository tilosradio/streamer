package hu.tilos.radio.backend.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.bson.types.ObjectId;
import org.dozer.CustomConverter;

public class ReferenceEncoder implements CustomConverter {

    private DB db;

    private String collection;

    private String[] fields;

    public ReferenceEncoder(DB db, String collection, String[] fields) {
        this.db = db;
        this.collection = collection;
        this.fields = fields;
    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        try {
            String id = (String) sourceClass.getMethod("getId", null).invoke(sourceFieldValue);
            if (id == null) {
                return null;
            }
            DBObject original = db.getCollection(collection).findOne(new BasicDBObject("_id", new ObjectId(id)));
            if (original == null) {
                throw new RuntimeException("Referenced " + collection + " doesn't exist. ID=" + id);
            }
            BasicDBObject reference = new BasicDBObject();
            for (String field : fields) {
                if (original.get(field) != null) {
                    reference.put(field, original.get(field));
                }
            }
            reference.put("ref", new DBRef(db, "show", original.get("_id").toString()));
            return reference;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
