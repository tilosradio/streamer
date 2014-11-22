package hu.tilos.radio.backend;

import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.User;
import hu.tilos.radio.backend.data.Token;
import hu.tilos.radio.backend.util.JWTEncoder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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
    ModelMapper modelMapper;

    @Inject
    @Configuration(name = "auth.url")
    private String serverUrl;

    @Inject
    private EntityManager entityManager;

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
        String bearer = servletRequest.getHeader("Bearer");
        if (bearer != null && bearer.length() > 10) {
            try {

                Token token = jwtEncoder.decode(bearer);

                User user = (User) entityManager.createNamedQuery("user.byUsername").setParameter("username", token.getUsername()).getSingleResult();

                session.setCurrentUser(user);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        Method m = resource.getResourceMethod();
        if (m.isAnnotationPresent(Security.class)) {
            Security s = m.getAnnotation(Security.class);
            if (s.role() != Role.GUEST && s.role() != Role.UNKNOWN) {
                User user = session.getCurrentUser();
                if (user == null || (s.role().ordinal() > user.getRole().ordinal())) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
                    return;
                }
            }
        } else {
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

}
