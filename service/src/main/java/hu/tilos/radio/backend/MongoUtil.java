package hu.tilos.radio.backend;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

public class MongoUtil {

    public static BasicDBObject aliasOrId(String aliasOrId) {
        BasicDBObject query;
        if (aliasOrId.matches("\\d{1,5}")) {
            query = new BasicDBObject("id", Integer.parseInt(aliasOrId));
        } else if (aliasOrId.matches("[abcdef0-9]{16,}")) {
            query = new BasicDBObject("_id", new ObjectId(aliasOrId));
        } else {
            query = new BasicDBObject("alias", aliasOrId);
        }
        return query;
    }
}
