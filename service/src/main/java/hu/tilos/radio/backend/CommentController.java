package hu.tilos.radio.backend;

import hu.radio.tilos.model.Comment;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.CommentStatus;
import hu.radio.tilos.model.type.CommentType;
import hu.tilos.radio.backend.converters.TagUtil;
import hu.tilos.radio.backend.data.CommentData;
import hu.tilos.radio.backend.data.CommentToSave;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.*;

@Path("/api/v1/comment")
public class CommentController {

    private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

    @Inject
    ModelMapper modelMapper;

    @Inject
    private EntityManager entityManager;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    Session session;

    @Inject
    TagUtil tagUtil;

    @GET
    @Path("/{type}/{identifier}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<CommentData> list(@PathParam("type") CommentType type, @PathParam("identifier") int id) {
        Query namedQuery = entityManager.createNamedQuery("comment.byTypeIdentifierStatusAuthor");
        namedQuery.setParameter("type", type);
        namedQuery.setParameter("identifier", id);
        namedQuery.setParameter("status", CommentStatus.ACCEPTED);
        namedQuery.setParameter("author", session.getCurrentUser());
        List<Comment> comments = namedQuery.getResultList();

        Map<Integer, CommentData> commentsById = new HashMap<>();

        for (Comment comment : comments) {
            commentsById.put(comment.getId(), modelMapper.map(comment, CommentData.class));
        }
        for (Comment comment : comments) {
            if (comment.getParent() != null) {
                commentsById.get(comment.getParent().getId()).getChildren().add(commentsById.get(comment.getId()));
            }
        }

        List<CommentData> topLevelComments = new ArrayList();

        for (Comment comment : comments) {
            if (comment.getParent() == null) {
                topLevelComments.add(commentsById.get(comment.getId()));
            }
        }

        return topLevelComments;
    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.AUTHOR)
    @Path("/{type}/{identifier}")
    @POST
    @Transactional
    public CreateResponse create(@PathParam("type") CommentType type, @PathParam("identifier") int id, CommentToSave data) {
        Comment comment = new Comment();
        comment.setMoment(data.getMoment());
        comment.setComment(data.getComment());
        comment.setAuthor(session.getCurrentUser());
        comment.setType(type);
        comment.setIdentifier(id);
        comment.setCreated(new Date());
        if (data.getParentId() > 0) {
            comment.setParent(entityManager.find(Comment.class, data.getParentId()));
        }

        entityManager.persist(comment);
        entityManager.flush();
        return new CreateResponse(comment.getId());
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }

}
