package hu.tilos.radio.backend.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.MixCategory;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.MixSimple;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

@Path("/api/v1/mix")
public class MixController {

    @Inject
    DozerBeanMapper mapper;

    @Inject
    DB db;


    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    public List<MixSimple> list(@QueryParam("show") String show, @QueryParam("category") String category) {

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

    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(hu.tilos.radio.backend.data.types.MixData objectToSave) {
        DBObject newObject = mapper.map(objectToSave, BasicDBObject.class);
        db.getCollection("mix").insert(newObject);
        return new CreateResponse(((ObjectId) newObject.get("_id")).toHexString());

    }

    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.ADMIN)
    @Transactional
    @PUT
    @Path("/{id}")
    public UpdateResponse update(@PathParam("id") String alias, hu.tilos.radio.backend.data.types.MixData objectToSave) {
        DBObject original = db.getCollection("mix").findOne(aliasOrId(alias));
        mapper.map(objectToSave, original);
        db.getCollection("mix").update(aliasOrId(alias), original);
        return new UpdateResponse(true);
    }

    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.ADMIN)
    @Transactional
    @DELETE
    @Path("/{id}")
    public boolean delete(@PathParam("id") String id) {
        db.getCollection("mix").remove(aliasOrId(id));
        return true;
    }


    @GET
    @Path("/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public hu.tilos.radio.backend.data.types.MixData get(@PathParam("id") String i) {
        hu.tilos.radio.backend.data.types.MixData r = mapper.map(db.getCollection("mix").findOne(aliasOrId(i)), hu.tilos.radio.backend.data.types.MixData.class);
        return r;
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
