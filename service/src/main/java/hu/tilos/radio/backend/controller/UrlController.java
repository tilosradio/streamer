package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.Show;
import hu.radio.tilos.model.Url;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.input.UrlToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.UrlData;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;

@Path("/api/v1/url")
public class UrlController {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ModelMapper mapper;

    @GET
    @Path("/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public UrlData get(@PathParam("id") int id) {
        Url content = selectUrl(id);
        return mapper.map(content, UrlData.class);
    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/{id}")
    @Security(role = Role.ADMIN)
    @PUT
    @Transactional
    public UpdateResponse update(@PathParam("id") int id, UrlToSave urlToSave) {
        Url content = selectUrl(id);
        mapper.map(urlToSave, content);
        entityManager.persist(content);
        entityManager.flush();
        return new UpdateResponse(true);
    }

    private Url selectUrl(int id) {
        return (Url) entityManager.createQuery("SELECT u from Url u where u.id = :id").setParameter("id", id).getSingleResult();
    }

    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(@QueryParam("show") int showId, UrlToSave urlToSave) {
        Url url = mapper.map(urlToSave, Url.class);
        Show show = entityManager.find(Show.class, showId);


        entityManager.persist(url);
        entityManager.flush();

        show.getUrls().add(url);
        entityManager.persist(show);
        return new CreateResponse(url.getId());
    }

    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/{id}")
    @Security(role = Role.ADMIN)
    @DELETE
    @Transactional
    public void delete(@PathParam("id") int id) {
        Url url = entityManager.find(Url.class, id);

        Show show = (Show) entityManager.createQuery("SELECT s FROM Show s JOIN FETCH  s.urls u WHERE u = :url").setParameter("url", url).getSingleResult();
        show.getUrls().remove(url);
        entityManager.persist(show);
        entityManager.remove(url);
        entityManager.flush();
    }


    public EntityManager getEntityManager() {
        return entityManager;
    }
}
