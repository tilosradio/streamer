package hu.tilos.radio.backend.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.converters.TagUtil;
import hu.tilos.radio.backend.data.input.EpisodeToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.ErrorResponse;
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
import javax.ws.rs.core.Response;

import java.util.*;

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
    @Path("/")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public Response listEpisodes(@QueryParam("start") long from, @QueryParam("end") long to) {
        Date fromDate = new Date();
        fromDate.setTime(from);
        Date toDate = new Date();
        toDate.setTime(to);

        if (to - from > 1000 * 60 * 60 * 168) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse("Period is too big")).build();
        }
        List<EpisodeData> episodeData = episodeUtil.getEpisodeData(null, fromDate, toDate);
        Collections.sort(episodeData, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData e1, EpisodeData e2) {
                return e1.getPlannedFrom().compareTo(e2.getPlannedFrom());
            }
        });
        return Response.ok(episodeData).build();

    }

    @GET
    @Path("/next")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<EpisodeData> next() {
        Date start = new Date();
        Date end = new Date();
        end.setTime(start.getTime() + (long) 168 * 60 * 60 * 1000);

        BasicDBObject query = new BasicDBObject();
        query.put("plannedFrom", new BasicDBObject("$lt", end));
        query.put("plannedTo", new BasicDBObject("$gt", start));

        DBCursor episodes = db.getCollection("episode").find(query).sort(new BasicDBObject("plannedFrom", 1)).limit(5);
        List<EpisodeData> result = new ArrayList<>();
        for (DBObject episode : episodes) {
            EpisodeData episodeData = modelMapper.map(episode, EpisodeData.class);
            EpisodeUtil.linkGenerator(episodeData);
            result.add(episodeData);
        }
        return result;
    }


    @GET
    @Path("/last")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<EpisodeData> last() {
        Date start = new Date();
        start.setTime(start.getTime() - (long) 30 * 24 * 60 * 60 * 1000);

        BasicDBObject query = new BasicDBObject();
        query.put("created", new BasicDBObject("$gt", start));
        query.put("text.content", new BasicDBObject("$exists", true));

        DBCursor episodes = db.getCollection("episode").find(query).sort(new BasicDBObject("created", -1)).limit(10);
        List<EpisodeData> result = new ArrayList<>();
        for (DBObject episode : episodes) {
            EpisodeData episodeData = modelMapper.map(episode, EpisodeData.class);
            EpisodeUtil.linkGenerator(episodeData);
            result.add(episodeData);
        }
        return result;
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
            return EpisodeUtil.linkGenerator(episodeData.get(0));
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
        newMongoOBject.put("created", new Date());
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
