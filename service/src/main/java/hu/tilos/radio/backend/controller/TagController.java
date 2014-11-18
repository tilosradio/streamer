package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.Tag;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.types.MixData;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/api/v1/tag")
public class TagController {

    @Inject
    ModelMapper modelMapper;
    @Inject
    private EntityManager entityManager;

    @GET
    @Path("/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public MixData get(@PathParam("id") int i) {
        Tag tag = entityManager.find(Tag.class, i);
        System.out.println(tag.getTaggedTexts().size());
        System.out.println(tag.getTaggedTexts().get(0));
        return null;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
