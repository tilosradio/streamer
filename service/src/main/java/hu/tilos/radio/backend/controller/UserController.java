package hu.tilos.radio.backend.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.AuthUtil;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.UserInfo;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


/**
 * Generate atom feed for the shows.
 */
@Path("/api/v1/user")
public class UserController {


    @Inject
    Session session;

    @Inject
    DozerBeanMapper mapper;

    @Inject
    DB db;

    @Path("/me")
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public UserInfo me() {
        DBObject userObject = db.getCollection("user").findOne(new BasicDBObject("username", session.getCurrentUser().getUsername()));
        UserInfo user = mapper.map(userObject, UserInfo.class);
        AuthUtil.calculatePermissions(user);
        return user;
    }


}
