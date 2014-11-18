package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.User;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.UserInfo;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
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
    private EntityManager entityManager;

    @Inject
    Session session;

    @Inject
    ModelMapper mapper;

    @Path("/me")
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public UserInfo me() {
        return mapper.map(entityManager.find(User.class, session.getCurrentUser().getId()), UserInfo.class);
    }


}
