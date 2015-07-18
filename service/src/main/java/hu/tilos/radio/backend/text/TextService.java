package hu.tilos.radio.backend.text;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.data.error.NotFoundException;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.OkResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.util.TextConverter;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import javax.ws.rs.PathParam;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;


public class TextService {

    @Inject
    private TextConverter textConverter;

    @Inject
    private DB db;

    @Inject
    private DozerBeanMapper mapper;

    public List<TextDataSimple> list(String type, Integer limit, boolean full) {
        checkType(type);
        BasicDBObject query = new BasicDBObject("type", type);
        DBCursor pages = db.getCollection(type).find(query).sort(new BasicDBObject("created", -1));
        if (limit != null) {
            pages.limit(limit);
        }
        if (full) {
            return pages.toArray().stream().map(text -> mapper
                    .map(text, TextData.class))
                    .map(textData -> {
                        textData.setFormatted(textConverter.format(textData.getFormat(), textData.getContent()));
                        return textData;
                    })
                    .collect(Collectors.toList());
        } else {
            return pages.toArray().stream()
                    .map(text -> mapper.map(text, TextDataSimple.class))
                    .collect(Collectors.toList());
        }
    }


    public TextData get(String alias, String type) {
        checkType(type);
        DBObject pageObject = db.getCollection(type).findOne(aliasOrId(alias));
        if (pageObject == null) {
            throw new NotFoundException("No such object");
        }
        TextData page = mapper.map(pageObject, TextData.class);
        page.setFormatted(textConverter.format(page.getFormat(), page.getContent()));
        return page;
    }


    public UpdateResponse update(@PathParam("type") String type, @PathParam("id") String alias, TextToSave objectToSave) {
        checkType(type);
        DBObject original = db.getCollection(type).findOne(aliasOrId(alias));
        mapper.map(objectToSave, original);
        original.put("format", "markdown");
        db.getCollection(type).update(aliasOrId(alias), original);
        return new UpdateResponse(true);
    }

    public OkResponse delete(@PathParam("type") String type, @PathParam("id") String alias) {
        checkType(type);
        DBObject original = db.getCollection(type).findOne(aliasOrId(alias));
        if (original == null) {
            throw new NotFoundException("No such text object");
        }
        db.getCollection(type).remove(aliasOrId(alias));
        return new OkResponse("document is deleted");
    }


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
