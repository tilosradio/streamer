package hu.tilos.radio.backend.text;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.input.TextToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.TextData;
import hu.tilos.radio.backend.data.types.TextDataSimple;
import hu.tilos.radio.backend.util.TextConverter;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

@Path("/api/v1/text")
public class TextController {

    @Inject
    private TextConverter textConverter;

    @Inject
    private DB db;

    @Inject
    private DozerBeanMapper mapper;

    @GET
    @Path("/{type}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public List<TextDataSimple> list(@PathParam("type") String type, @QueryParam("limit") Integer limit) {
        checkType(type);
        BasicDBObject query = new BasicDBObject("type", type);
        DBCursor pages = db.getCollection(type).find(query).sort(new BasicDBObject("created", -1));
        if (limit != null) {
            pages.limit(limit);
        }
        List<TextDataSimple> result = new ArrayList<>();
        for (DBObject page : pages) {
            result.add(mapper.map(page, TextDataSimple.class));
        }
        return result;
    }

    @GET
    @Path("/{type}/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public TextData get(@PathParam("id") String alias, @PathParam("type") String type) {
        checkType(type);
        TextData page = mapper.map(db.getCollection(type).findOne(aliasOrId(alias)), TextData.class);
        page.setFormatted(textConverter.format(page.getFormat(), page.getContent()));
        return page;
    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/{type}/{id}")
    @Security(role = Role.ADMIN)
    @PUT
    @Transactional
    public UpdateResponse update(@PathParam("type") String type, @PathParam("id") String alias, TextToSave objectToSave) {
        checkType(type);
        DBObject original = db.getCollection(type).findOne(aliasOrId(alias));
        mapper.map(objectToSave, original);
        original.put("format", "markdown");
        db.getCollection(type).update(aliasOrId(alias), original);
        return new UpdateResponse(true);
    }

    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/{type}")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(@PathParam("type") String type, TextToSave objectToSave) {
        checkType(type);
        DBObject newObject = mapper.map(objectToSave, BasicDBObject.class);
        newObject.put("format", "markdown");
        newObject.put("type", type);
        newObject.put("created", new Date());
        db.getCollection(type).insert(newObject);
        return new CreateResponse(((ObjectId) newObject.get("_id")).toHexString());
    }

    private void checkType(String type) {
        if (type.equals("page") || type.equals("news") || type.equals("adminnews")) {
            return;
        } else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
    }


    public void setDb(DB db) {
        this.db = db;
    }
}
