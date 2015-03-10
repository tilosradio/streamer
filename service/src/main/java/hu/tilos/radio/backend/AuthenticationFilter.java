package hu.tilos.radio.backend;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.data.Token;
import hu.tilos.radio.backend.data.UserInfo;
import hu.tilos.radio.backend.util.JWTEncoder;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    private static Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Context
    ResourceInfo resource;

    @Context
    HttpServletRequest servletRequest;

    @Inject
    Session session;

    @Inject
    DozerBeanMapper modelMapper;

    @Inject
    @Configuration(name = "auth.url")
    private String serverUrl;

    @Inject
    private DB db;

    @Inject
    private JWTEncoder jwtEncoder;


    public AuthenticationFilter() {

    }

    private String getAuthUrl() {
        return serverUrl;
    }

    @Override
    @Transactional()
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String bearer = servletRequest.getHeader("Authorization") !=null ? servletRequest.getHeader("Authorization").replace("Bearer ", "") : null;
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


        Method m = resource.getResourceMethod();
        if (session.getCurrentUser() != null && session.getCurrentUser().getRole().equals(Role.ADMIN)) {
            return;
        }
        if (m.isAnnotationPresent(Security.class)) {
            Security s = m.getAnnotation(Security.class);
            UserInfo user = session.getCurrentUser();
            if (s.permission() != null && s.permission().length() > 0) {
                //resolve path
                String resolvedPath = resolvePath(s.permission(), requestContext);
                if (user == null) {
                    if (resolvedPath.equals("all")) {
                        return;
                    } else {
                        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                    }
                } else {
                    for (String permission : user.getPermissions()) {
                        if (permission.equals(resolvedPath)) {
                            return;
                        }
                    }
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                }

            } else if (s.role() != Role.GUEST && s.role() != Role.UNKNOWN) {
                //role based access control

                if (user == null || (s.role().ordinal() > user.getRole().ordinal())) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                    return;
                }
            }
        } else {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    private String resolvePath(String permission, ContainerRequestContext requestContext) {
        for (String pathKey : requestContext.getUriInfo().getPathParameters().keySet()) {
            String value = requestContext.getUriInfo().getPathParameters().getFirst(pathKey);
            permission = permission.replaceAll("\\{" + pathKey + "\\}", value);
        }
        return permission;
    }

}
