package hu.tilos.radio.backend.controller;

import com.mongodb.*;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.UserInfo;
import hu.tilos.radio.backend.data.input.AuthorToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.AuthorDetailed;
import hu.tilos.radio.backend.data.types.AuthorListElement;
import hu.tilos.radio.backend.data.types.UserDetailed;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

@Path("/api/v1/author")
public class AuthorController {

    private static Logger LOG = LoggerFactory.getLogger(AuthorController.class);

    @Inject
    Session session;

    @Inject
    private DozerBeanMapper mapper;

    @Inject
    private DB db;

    @Inject
    AvatarLocator avatarLocator;

    @Produces("application/json")
    @Path("/")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public List<AuthorListElement> list() {

        DBCursor selectedAuthors = db.getCollection("author").find();

        List<AuthorListElement> mappedAuthors = new ArrayList<>();
        for (DBObject author : selectedAuthors) {
            mappedAuthors.add(mapper.map(author, AuthorListElement.class));
        }
        return mappedAuthors;

    }

    @Produces("application/json")
    @Path("/{alias}")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public AuthorDetailed get(@PathParam("alias") String alias) {
        DBObject one = findAuthor(alias);
        AuthorDetailed author = mapper.map(one, AuthorDetailed.class);
        avatarLocator.locateAvatar(author);
        if (session.getCurrentUser() != null && (session.getCurrentUser().getRole() == Role.ADMIN || session.getCurrentUser().getRole() == Role.AUTHOR)) {
            author.setEmail((String) one.get("email"));
        }
        return author;

    }

    private DBObject findAuthor(String alias) {
        BasicDBObject query = aliasOrId(alias);
        return db.getCollection("author").findOne(query);
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
        DBObject author = findAuthor(alias);
        checkPermission(author, session.getCurrentUser());
        mapper.map(authorToSave, author);
        db.getCollection("author").update(aliasOrId(alias), author);
        return new UpdateResponse(true);

    }

    protected void checkPermission(DBObject author, UserInfo currentUser) {

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        DBObject originalUser = db.getCollection("user").findOne(new BasicDBObject("_id", new ObjectId(currentUser.getId())));
        DBRef authorRef = (DBRef) originalUser.get("author");
        if (authorRef!=null && authorRef.getId().equals(author.get("_id").toString())){
            return;
        }

        throw new IllegalArgumentException("No permission to modify");
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
        DBObject author = mapper.map(authorToSave, BasicDBObject.class);
        db.getCollection("author").insert(author);
        return new CreateResponse(((ObjectId) author.get("_id")).toHexString());

    }

    public void setDb(DB db) {
        this.db = db;
    }
}
