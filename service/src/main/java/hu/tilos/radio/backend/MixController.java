package hu.tilos.radio.backend;

import hu.radio.tilos.model.Mix;
import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.converters.*;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.data.types.MixData;
import hu.tilos.radio.backend.data.MixResponse;
import hu.tilos.radio.backend.data.types.MixSimple;
import org.dozer.DozerBeanMapper;
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

import static org.dozer.loader.api.FieldsMappingOptions.customConverter;

@Path("/api/v1/mix")
public class MixController {

    @Inject
    ModelMapper modelMapper;
    BeanMappingBuilder retrieveBuilder = new BeanMappingBuilder() {

        @Override
        protected void configure() {
            mapping(Mix.class, MixResponse.class)
                    .fields("type", "type")
                    .fields("date", "date", new FieldsMappingOption() {
                        @Override
                        public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                            fieldMappingBuilder.customConverter(DateToTextConverter.class);
                            fieldMappingBuilder.customConverterParam("yyyy-MM-dd");
                        }
                    });
        }
    };
    BeanMappingBuilder updateBuilder = new BeanMappingBuilder() {

        @Override
        protected void configure() {
            mapping(MixData.class, Mix.class)
                    .fields("show", "show", new FieldsMappingOption() {
                        @Override
                        public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                            fieldMappingBuilder.customConverterId(ChildEntityFieldConverter.ID);
                        }
                    })
                    .fields("date", "date", new FieldsMappingOption() {
                        @Override
                        public void apply(DozerBuilder.FieldMappingBuilder fieldMappingBuilder) {
                            fieldMappingBuilder.customConverter(DateToTextConverter.class);
                            fieldMappingBuilder.customConverterParam("yyyy-MM-dd");
                        }
                    })
                    .exclude("id");


        }
    };
    @PersistenceContext
    private EntityManager entityManager;


    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    public List<MixSimple> list(@QueryParam("show") String show, @QueryParam("category") String category) {

        String query = "SELECT m from Mix m";
        if (show != null) {
            query += " LEFT JOIN m.show s WHERE s.alias = :alias";
        }
        if (category != null) {
            query += " WHERE m.category = :category";
        }
        Query q = entityManager.createQuery(query, Mix.class);
        if (show != null) {
            q.setParameter("alias", show);
        }
        if (category != null) {
            q.setParameter("category", MixCategory.valueOf(category));
        }
        List<Mix> mixes = q.getResultList();

        List<MixSimple> response = new ArrayList<>();
        for (Mix mix : mixes) {
            response.add(modelMapper.map(mix, MixSimple.class));
        }

        return response;

    }

    /**
     *
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(MixData newMix) {

        Mix mix = modelMapper.map(newMix, Mix.class);

        entityManager.persist(mix);

        return new CreateResponse(mix.getId());

    }

    /**
     *
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.ADMIN)
    @Transactional
    @PUT
    @Path("/{id}")
    public CreateResponse update(@PathParam("id") int id, MixData newMix) {

        Mix mix = entityManager.find(Mix.class, id);

        modelMapper.map(newMix, mix);

        return new CreateResponse(mix.getId());
    }

    /**
     *
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.ADMIN)
    @Transactional
    @DELETE
    @Path("/{id}")
    public boolean delete(@PathParam("id") int id) {

        Mix mix = entityManager.find(Mix.class, id);

        entityManager.remove(entityManager.find(Mix.class, id));

        return true;
    }


    @GET
    @Path("/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public MixData get(@PathParam("id") int i) {
        MixData r = modelMapper.map(entityManager.find(Mix.class, i), MixData.class);
        return r;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
