package hu.tilos.radio.backend.comment;

import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.CommentType;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.response.CreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.List;

@Path("/api/v1/comment")
public class CommentController {

    private static final Logger LOG = LoggerFactory.getLogger(CommentController.class);

    @Inject
    CommentService commentService;

    @Inject
    Session session;

    @GET
    @Path("/{type}/{identifier}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<CommentData> list(@PathParam("type") CommentType type, @PathParam("identifier") String id) {
        return commentService.list(type, id, session);
    }

    @GET
    @Path("/")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<CommentData> listAll(@QueryParam("status") String status) {
        return commentService.listAll(status);
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
        return commentService.approve(id);
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
        commentService.delete(id);
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
        return commentService.create(type, id, data, session);

    }


}
