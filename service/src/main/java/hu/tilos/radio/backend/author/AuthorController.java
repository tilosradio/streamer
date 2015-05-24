package hu.tilos.radio.backend.author;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.input.AuthorToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.AuthorDetailed;
import hu.tilos.radio.backend.data.types.AuthorListElement;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.List;

@Path("/api/v1/author")
public class AuthorController {

    private static Logger LOG = LoggerFactory.getLogger(AuthorController.class);

    @Inject
    AuthorService authorService;

    @Produces("application/json")
    @Path("/")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public List<AuthorListElement> list() {
        return authorService.list();

    }

    @Produces("application/json")
    @Path("/{alias}")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public AuthorDetailed get(@PathParam("alias") String alias) {
        return authorService.get(alias);
    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/{alias}")
    @Security(permission = "/author/{alias}")
    @PUT
    @Transactional
    public UpdateResponse update(@PathParam("alias") String alias, AuthorToSave authorToSave) {
        return authorService.update(alias, authorToSave);

    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(AuthorToSave authorToSave) {
        return authorService.create(authorToSave);

    }

}
