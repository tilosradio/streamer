package hu.tilos.radio.backend.tag;

import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;

@Path("api/v1/tag")
public class TagController {

    private static final Logger LOG = LoggerFactory.getLogger(TagController.class);

    @Inject
    TagService tagService;

    @GET
    @Path("/{tag}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public TaggedElementList get(@PathParam("tag") String tag) {
        return tagService.get(tag);
    }

    @GET
    @Path("/")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public TagCloud list(@QueryParam("limit") Integer limit) {
        return tagService.list(limit);
    }

}
