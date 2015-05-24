package hu.tilos.radio.backend.spark;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.auth.AuthUtil;
import hu.tilos.radio.backend.data.Token;
import hu.tilos.radio.backend.user.UserInfo;
import hu.tilos.radio.backend.util.JWTEncoder;
import org.dozer.DozerBeanMapper;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.halt;

public class Authorized implements Route {

    @Inject
    DB db;

    @Inject
    DozerBeanMapper modelMapper;

    @Inject
    JWTEncoder jwtEncoder;

    Role role;

    String permission;

    AuthorizedRoute route;

    public Authorized(Role admin, AuthorizedRoute rule) {
        this.role = admin;
        this.route = rule;
    }

    public Authorized(String permission, AuthorizedRoute route) {
        this.permission = permission;
        this.route = route;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Session session = new Session();
        String bearer = request.headers("Authorization") != null ? request.headers("Authorization").replace("Bearer ", "") : null;
        if (bearer != null && bearer.length() > 10) {
            try {

                Token token = jwtEncoder.decode(bearer);

                UserInfo user = modelMapper.map(db.getCollection("user").findOne(new BasicDBObject("username", token.getUsername())), UserInfo.class);
                AuthUtil.calculatePermissions(user);
                session.setCurrentUser(user);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        if (session.getCurrentUser() != null && session.getCurrentUser().getRole().equals(Role.ADMIN)) {
            return route.handle(request, response, session);
        }


        UserInfo user = session.getCurrentUser();
        if (permission != null && permission.length() > 0) {
            //resolve path
            String resolvedPath = resolvePath(permission, request);
            if (user == null) {
                if (resolvedPath.equals("all")) {
                    return route.handle(request, response, session);
                } else {
                    halt(403, "Access denied");
                }
            } else {
                for (String permission : user.getPermissions()) {
                    if (permission.equals(resolvedPath)) {
                        return route.handle(request, response, session);
                    }
                }
                halt(403, "Access denied");
            }

        } else if (role != Role.GUEST && role != Role.UNKNOWN) {
            //role based access control

            if (user == null || (role.ordinal() > user.getRole().ordinal())) {
                halt(403, "Access denied");
            }
        }

        return route.handle(request, response, null);
    }

    private String resolvePath(String permission, Request request) {
        return permission;
    }
}
