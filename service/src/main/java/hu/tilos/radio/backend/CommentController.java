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

    @GET
    @Path("/")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<CommentData> listAll(@QueryParam("status") String status) {
        Query selectComments;
        if (status == null) {
            selectComments = entityManager.createQuery("SELECT c FROM Comment c ORDER BY c.created desc");
        } else {
            selectComments = entityManager.createQuery("SELECT c FROM Comment c WHERE c.status = :status ORDER BY c.created desc").setParameter("status", CommentStatus.valueOf(status));
        }

        List<Comment> comments = selectComments.getResultList();

        List<CommentData> commentDtos = new ArrayList();
        for (Comment comment : comments) {
            commentDtos.add(modelMapper.map(comment, CommentData.class));
        }

        return commentDtos;
    }

    @POST
    @Path("/approve/{id}")
    @Security(role = Role.ADMIN)
    @Produces("application/json")
    @Transactional
    public CommentData approve(@PathParam("id") int id) {
        Comment comment = entityManager.find(Comment.class, id);
        comment.setStatus(CommentStatus.ACCEPTED);
        entityManager.persist(comment);
        return modelMapper.map(comment, CommentData.class);
    }

    @DELETE
    @Path("/approve/{id}")
    @Security(role = Role.ADMIN)
    @Produces("application/json")
    @Transactional
    public void delete(@PathParam("id") int id) {
        Comment comment = entityManager.find(Comment.class, id);

        entityManager.remove(comment);
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
