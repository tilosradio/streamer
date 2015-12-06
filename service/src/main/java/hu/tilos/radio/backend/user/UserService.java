package hu.tilos.radio.backend.user;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.auth.AuthUtil;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class UserService {

    @Inject
    DozerBeanMapper mapper;

    @Inject
    DB db;

    public UserInfo me(Session session) {
        DBObject userObject = db.getCollection("user").findOne(new BasicDBObject("username", session.getCurrentUser().getUsername()));
        UserInfo user = mapper.map(userObject, UserInfo.class);
        AuthUtil.calculatePermissions(user);
        return user;
    }

    public List<UserInfo> list() {
        DBCursor users = db.getCollection("user").find();
        List<UserInfo> userList = new ArrayList();
        while (users.hasNext()) {
            userList.add(mapper.map(users.next(), UserInfo.class));
        }
        return userList;
    }


}
