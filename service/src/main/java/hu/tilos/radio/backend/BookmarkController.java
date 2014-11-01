package hu.tilos.radio.backend;

import hu.radio.tilos.model.Bookmark;
import hu.radio.tilos.model.Mix;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.MixCategory;
import hu.tilos.radio.backend.converters.ChildEntityFieldConverter;
import hu.tilos.radio.backend.converters.DateToTextConverter;
import hu.tilos.radio.backend.data.*;
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

/**
 * Function to handle bookmarks.
 */
@Path("/api/v1/bookmark")
public class BookmarkController {

    @Inject
    ModelMapper modelMapper;

    @Inject
    private EntityManager entityManager;

    /**
     * Endpoint to get bookmarks of a radioshow.
     */
    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    public List<BookmarkSimple> list(@QueryParam("show") String show) {

        String query = "SELECT b from Bookmark b";
        if (show != null) {
            query += " LEFT JOIN b.show s WHERE s.alias = :alias";
        }
        Query q = entityManager.createQuery(query, Bookmark.class);
        if (show != null) {
            q.setParameter("alias", show);
        }
        List<Bookmark> bookmarks = q.getResultList();

        List<BookmarkSimple> response = new ArrayList<>();
        for (Bookmark bm : bookmarks) {
            response.add(modelMapper.map(bm, BookmarkSimple.class));
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
    public CreateResponse create(BookmarkData data) {

        Bookmark entity = modelMapper.map(data, Bookmark.class);

        entityManager.persist(entity);

        return new CreateResponse(entity.getId());

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
    public UpdateResponse update(@PathParam("id") int id, BookmarkData inputData) {

        Bookmark entity = entityManager.find(Bookmark.class, id);

        modelMapper.map(inputData, entity);

        return new UpdateResponse(entity.getId());
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
