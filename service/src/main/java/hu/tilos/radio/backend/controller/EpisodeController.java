package hu.tilos.radio.backend.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.converters.TagUtil;
import hu.tilos.radio.backend.data.input.EpisodeToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.data.types.TagData;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

@Path("/api/v1/episode")
public class EpisodeController {

    private static final Logger LOG = LoggerFactory.getLogger(EpisodeController.class);

    @Inject
    DozerBeanMapper modelMapper;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    Session session;

    @Inject
    TagUtil tagUtil;

    @Inject
    DB db;

    @GET
    @Path("/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public EpisodeData get(@PathParam("id") String id) {
        DBObject episode = db.getCollection("episode").findOne(aliasOrId(id));
        EpisodeData r = modelMapper.map(episode, EpisodeData.class);
        episodeUtil.linkGenerator(r);
        return r;
    }

    @GET
    @Path("/{show}/{year}/{month}/{day}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public EpisodeData getByDate(@PathParam("show") String showAlias, @PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) {
        BasicDBObject show = (BasicDBObject) db.getCollection("show").findOne(aliasOrId(showAlias));
        String showId = show.get("_id").toString();
        List<EpisodeData> episodeData = episodeUtil.getEpisodeData(showId, new Date(year - 1900, month - 1, day), new Date(year - 1900, month - 1, day, 23, 59, 59));
        if (episodeData.size() == 0) {
            //todo, error handling
            throw new IllegalArgumentException("Can't find the appropriate episode");
        } else {
            return episodeData.get(0);
        }
    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.AUTHOR)
    @POST
    @Transactional
    public CreateResponse create(EpisodeToSave entity) {


        if (entity.getText() != null) {
            entity.getText().setAlias("");
            entity.getText().setFormat("default");
            entity.getText().setType("episode");
        }
        if (entity.getRealFrom() == null) {
            entity.setRealFrom(entity.getPlannedFrom());
        }
        if (entity.getRealTo() == null) {
            entity.setRealTo(entity.getPlannedTo());
        }


        BasicDBObject newMongoOBject = modelMapper.map(entity, BasicDBObject.class);

        newMongoOBject.remove("id");
        newMongoOBject.remove("persistent");
        //FIXME
        updateTags(entity);
        db.getCollection("episode").insert(newMongoOBject);

        return new CreateResponse(((ObjectId) newMongoOBject.get("_id")).toHexString());
    }

    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.AUTHOR)
    @Transactional
    @PUT
    @Path("/{id}")
    public UpdateResponse update(@PathParam("id") String alias, EpisodeToSave objectToSave) {

        if (objectToSave.getText() != null) {
            objectToSave.getText().setAlias("");
            objectToSave.getText().setFormat("default");
            objectToSave.getText().setType("episode");
        }

        updateTags(objectToSave);
        DBObject original = db.getCollection("episode").findOne(aliasOrId(alias));
        modelMapper.map(objectToSave, original);
        db.getCollection("episode").update(aliasOrId(alias), original);
        return new UpdateResponse(true);
    }


    public void updateTags(EpisodeToSave episode) {
        if (episode.getText() != null && episode.getText().getContent() != null) {
            Set<TagData> newTags = tagUtil.getTags(episode.getText().getContent());
            episode.setTags(newTags);

        }
    }


    public void setDb(DB db) {
        this.db = db;
    }
}
