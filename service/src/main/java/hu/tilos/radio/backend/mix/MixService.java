package hu.tilos.radio.backend.mix;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import hu.radio.tilos.model.type.MixCategory;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

public class MixService {

    @Inject
    DozerBeanMapper mapper;

    @Inject
    DB db;


    public List<MixSimple> list(String show, String category) {

        BasicDBObject query = new BasicDBObject();
        if (show != null) {
            DBObject mongoShow = db.getCollection("show").findOne(aliasOrId(show));
            query.put("show.ref", new DBRef(db, "show", mongoShow.get("_id").toString()));
        }
        if (category != null) {
            query.put("category", MixCategory.valueOf(category.toUpperCase()).ordinal());
        }
        BasicDBObject sort = new BasicDBObject("date", -1);
        sort.append("id", -1);


        List<MixSimple> response = new ArrayList<>();
        for (DBObject mix : db.getCollection("mix").find(query).sort(sort)) {
            response.add(mapper.map(mix, MixSimple.class));
        }

        return response;

    }


    public CreateResponse create(MixData objectToSave) {
        DBObject newObject = mapper.map(objectToSave, BasicDBObject.class);
        db.getCollection("mix").insert(newObject);
        return new CreateResponse(((ObjectId) newObject.get("_id")).toHexString());

    }


    public UpdateResponse update(String alias, MixData objectToSave) {
        DBObject original = db.getCollection("mix").findOne(aliasOrId(alias));
        mapper.map(objectToSave, original);
        db.getCollection("mix").update(aliasOrId(alias), original);
        return new UpdateResponse(true);
    }


    public boolean delete(String id) {
        db.getCollection("mix").remove(aliasOrId(id));
        return true;
    }


    public void setDb(DB db) {
        this.db = db;
    }

    public MixData get(String i) {
        MixData r = mapper.map(db.getCollection("mix").findOne(aliasOrId(i)), MixData.class);
        return r;
    }
}
