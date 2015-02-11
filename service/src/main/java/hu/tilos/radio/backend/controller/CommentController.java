package hu.tilos.radio.backend.controller;

import com.mongodb.*;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.CommentStatus;
import hu.radio.tilos.model.type.CommentType;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.converters.TagUtil;
import hu.tilos.radio.backend.data.input.CommentToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.types.CommentData;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.*;

@Path("/api/v1/comment")
public class CommentController {

    private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

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
    @Path("/{type}/{identifier}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<CommentData> list(@PathParam("type") CommentType type, @PathParam("identifier") String id) {
        BasicDBObject query = new BasicDBObject();
        query.put("type", type.ordinal());
        query.put("identifier", id);

        BasicDBList or = new BasicDBList();
        or.add(new BasicDBObject("status", CommentStatus.ACCEPTED.ordinal()));
        or.add(new BasicDBObject("author.ref", new DBRef(db, "user", session.getCurrentUser().getId())));

        query.put("$or", or);

        DBCursor comments = db.getCollection("comment").find(query);

        Map<String, CommentData> commentsById = new HashMap<>();

        for (DBObject comment : comments) {
            commentsById.put(comment.get("_id").toString(), modelMapper.map(comment, CommentData.class));
        }
        for (DBObject comment : comments) {
            if (comment.get("parent") != null) {
                commentsById.get((String) comment.get("parent")).getChildren().add(commentsById.get(comment.get("_id").toString()));
            }
        }

        List<CommentData> topLevelComments = new ArrayList();

        for (DBObject comment : comments) {
            if (comment.get("parent") == null) {
                topLevelComments.add(commentsById.get(comment.get("_id").toString()));
            }
        }

        return topLevelComments;
    }

    @GET
    @Path("/")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<CommentData> listAll(@QueryParam("status") String status) {
        BasicDBObject query = new BasicDBObject();
        if (status != null) {
            query.put("status", CommentStatus.valueOf(status).ordinal());
        }
        DBCursor comments = db.getCollection("comment").find(query).sort(new BasicDBObject("created", -1));
        List<CommentData> commentDtos = new ArrayList();
        for (DBObject comment : comments) {
            commentDtos.add(modelMapper.map(comment, CommentData.class));
        }

        return commentDtos;
    }

    /**
     * @exclude
     */
    @POST
    @Path("/approve/{id}")
    @Security(role = Role.ADMIN)
    @Produces("application/json")
    @Transactional
    public CommentData approve(@PathParam("id") String id) {
        DBObject comment = db.getCollection("comment").findOne(new BasicDBObject("_id", new ObjectId(id)));
        comment.put("status", CommentStatus.ACCEPTED.ordinal());
        db.getCollection("comment").update(new BasicDBObject("_id", new ObjectId(id)), comment);
        return modelMapper.map(comment, CommentData.class);
    }

    /**
     * @exclude
     */
    @DELETE
    @Path("/approve/{id}")
    @Security(role = Role.ADMIN)
    @Produces("application/json")
    @Transactional
    public void delete(@PathParam("id") String id) {
        db.getCollection("comment").remove(new BasicDBObject("_id", new ObjectId(id)));
    }


    /**
     * me
     *
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.USER)
    @Path("/{type}/{identifier}")
    @POST
    @Transactional
    public CreateResponse create(@PathParam("type") CommentType type, @PathParam("identifier") String id, CommentToSave data) {
        BasicDBObject comment = modelMapper.map(data, BasicDBObject.class);
        comment.put("type", type.ordinal());
        comment.put("identifier", id);
        comment.put("created", new Date());
        comment.put("status", CommentStatus.NEW.ordinal());
        if (data.getParentId() != null) {
            comment.put("parent", data.getParentId());
        }

        BasicDBObject author = new BasicDBObject();
        author.put("username", session.getCurrentUser().getUsername());
        author.put("ref", new DBRef(db, "user", session.getCurrentUser().getId()));

        comment.put("author", author);

        db.getCollection("comment").insert(comment);
        return new CreateResponse(comment.get("_id").toString());
    }


}
