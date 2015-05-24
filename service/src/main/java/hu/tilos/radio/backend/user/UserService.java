package hu.tilos.radio.backend.user;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.auth.AuthUtil;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


public class UserService {

    @Inject
    DozerBeanMapper mapper;

    @Inject
    DB db;

    @Path("/me")
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public UserInfo me(Session session) {
        DBObject userObject = db.getCollection("user").findOne(new BasicDBObject("username", session.getCurrentUser().getUsername()));
        UserInfo user = mapper.map(userObject, UserInfo.class);
        AuthUtil.calculatePermissions(user);
        return user;
    }


}
