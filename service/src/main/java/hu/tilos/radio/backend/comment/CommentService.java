package hu.tilos.radio.backend.comment;

import com.mongodb.*;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.tag.TagUtil;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

public class CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    @Inject
    DozerBeanMapper modelMapper;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    TagUtil tagUtil;

    @Inject
    DB db;

    public List<CommentData> list(CommentType type, String id, Session session) {
        BasicDBObject query = new BasicDBObject();
        query.put("type", type.ordinal());
        query.put("identifier", id);

        if (session != null && session.getCurrentUser() != null) {
            BasicDBList or = new BasicDBList();
            or.add(new BasicDBObject("status", CommentStatus.ACCEPTED.ordinal()));
            or.add(new BasicDBObject("author.ref", new DBRef(db, "user", session.getCurrentUser().getId())));
            query.put("$or", or);
        } else {
            query.put("status", CommentStatus.ACCEPTED.ordinal());
        }

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

    public List<CommentData> listAll(String status) {
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

    public CommentData approve(String id) {
        DBObject comment = db.getCollection("comment").findOne(new BasicDBObject("_id", new ObjectId(id)));
        comment.put("status", CommentStatus.ACCEPTED.ordinal());
        db.getCollection("comment").update(new BasicDBObject("_id", new ObjectId(id)), comment);
        return modelMapper.map(comment, CommentData.class);
    }

    public void delete(String id) {
        db.getCollection("comment").remove(new BasicDBObject("_id", new ObjectId(id)));
    }


    public CreateResponse create(CommentType type, String id, CommentToSave data, Session session) {
        BasicDBObject comment = modelMapper.map(data, BasicDBObject.class);
        comment.put("type", type.ordinal());
        comment.put("identifier", id);
        comment.put("created", new Date());

        if (data.getParentId() != null) {
            comment.put("parent", data.getParentId());
        }

        BasicDBObject author = new BasicDBObject();
        author.put("username", session.getCurrentUser().getUsername());
        author.put("ref", new DBRef(db, "user", session.getCurrentUser().getId()));

        if (session.getCurrentUser().getRole().ordinal() >= Role.AUTHOR.ordinal()) {
            comment.put("status", CommentStatus.ACCEPTED.ordinal());
        } else {
            comment.put("status", CommentStatus.NEW.ordinal());
        }
        comment.put("author", author);

        db.getCollection("comment").insert(comment);
        return new CreateResponse(comment.get("_id").toString());
    }


}
