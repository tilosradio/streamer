package hu.tilos.radio.backend;

import hu.radio.tilos.model.Mix;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.Tag;
import hu.radio.tilos.model.type.MixCategory;
import hu.tilos.radio.backend.converters.ChildEntityFieldConverter;
import hu.tilos.radio.backend.converters.DateToTextConverter;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.data.MixResponse;
import hu.tilos.radio.backend.data.types.MixData;
import hu.tilos.radio.backend.data.types.MixSimple;
import org.dozer.loader.DozerBuilder;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOption;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

@Path("/api/v1/tag")
public class TagController {

    @Inject
    private EntityManager entityManager;

    @Inject
    ModelMapper modelMapper;

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
