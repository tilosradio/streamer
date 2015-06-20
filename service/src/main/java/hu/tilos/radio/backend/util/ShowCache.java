package hu.tilos.radio.backend.util;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.radio.tilos.model.type.ShowType;
import hu.tilos.radio.backend.show.ShowSimple;
import org.bson.types.ObjectId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShowCache {

    @Inject
    DB db;

    private static Map<String, ShowSimple> showCache = new ConcurrentHashMap<>();

    public ShowSimple getShowSimple(String id) {
        if (!showCache.containsKey(id)) {
            DBObject one = db.getCollection("show").findOne(new BasicDBObject("_id", new ObjectId(id)));
            ShowSimple show = new ShowSimple();
            show.setType(ShowType.values()[(Integer) one.get("type")]);
            show.setAlias((String) one.get("alias"));
            show.setName((String) one.get("name"));
            show.setId(id);
            showCache.put(show.getId(), show);
        }
        return showCache.get(id);
    }
}
