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
        query.put("status", CommentStatus.ACCEPTED.ordinal());
        //FIXME status or current author
        DBCursor comments = db.getCollection("comment").find(query);

        Map<String, CommentData> commentsById = new HashMap<>();

        for (DBObject comment : comments) {
            commentsById.put(comment.get("_id").toString(), modelMapper.map(comment, CommentData.class));
        }
        for (DBObject comment : comments) {
            if (comment.get("parent") != null) {
                DBRef parent = (DBRef) comment.get("parent");
                commentsById.get((String) parent.getId()).getChildren().add(commentsById.get(comment.get("_id").toString()));
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
        DBObject comment = db.getCollection("comment").findOne(new BasicDBObject("_id", id));
        comment.put("status", CommentStatus.ACCEPTED);
        db.getCollection("comment").update(new BasicDBObject("_id", id), comment);
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
    public void delete(@PathParam("id") int id) {
        db.getCollection("comment").remove(new BasicDBObject("_id", id));
    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.USER)
    @Path("/{type}/{identifier}")
    @POST
    @Transactional
    public CreateResponse create(@PathParam("type") CommentType type, @PathParam("identifier") int id, CommentToSave data) {
        BasicDBObject comment = modelMapper.map(data, BasicDBObject.class);
        comment.put("type", type.ordinal());
        comment.put("identifier", id);
        comment.put("created", new Date());

        BasicDBObject author = new BasicDBObject();
        author.put("username", session.getCurrentUser().getUsername());
        author.put("ref", new DBRef(db, "user", session.getCurrentUser().getId()));

        comment.put("creator", author);

        db.getCollection("comment").insert(comment);
        return new CreateResponse((String) comment.get("_id"));
    }


}
