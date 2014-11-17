package hu.tilos.radio.backend;

import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.Scheduling;
import hu.radio.tilos.model.Show;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.data.UpdateResponse;
import hu.tilos.radio.backend.data.input.SchedulingToSave;
import hu.tilos.radio.backend.data.types.SchedulingData;
import org.modelmapper.ModelMapper;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import javax.ws.rs.*;

@Path("/api/v1/scheduling")
public class SchedulingController {

    @Inject
    private EntityManager entityManager;

    @Inject
    private ModelMapper mapper;

    @GET
    @Path("/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public SchedulingData get(@PathParam("id") int id) {
        Scheduling content = selectScheduling(id);
        return mapper.map(content, SchedulingData.class);
    }


    @Produces("application/json")
    @Path("/{id}")
    @Security(role = Role.ADMIN)
    @PUT
    @Transactional
    public UpdateResponse update(@PathParam("id") int id, SchedulingToSave schedulingToSave) {
        Scheduling content = selectScheduling(id);
        mapper.map(schedulingToSave, content);
        entityManager.persist(content);
        entityManager.flush();
        return new UpdateResponse(true);
    }

    @Produces("application/json")
    @Path("/")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(@QueryParam("show") int showId, SchedulingToSave schedulingToSave) {
        Scheduling scheduling = mapper.map(schedulingToSave, Scheduling.class);
        Show show = entityManager.find(Show.class, showId);
        scheduling.setShow(show);
        entityManager.persist(scheduling);
        entityManager.flush();
        show.getSchedulings().add(scheduling);
        entityManager.persist(show);
        return new CreateResponse(scheduling.getId());
    }

    @Produces("application/json")
    @Path("/{id}")
    @Security(role = Role.ADMIN)
    @DELETE
    @Transactional
    public void delete(@PathParam("id") int id) {
        Scheduling scheduling = entityManager.find(Scheduling.class, id);
        Show show = scheduling.getShow();
        show.getSchedulings().remove(scheduling);
        entityManager.remove(scheduling);
        entityManager.persist(show);
        entityManager.flush();
    }

    public Scheduling selectScheduling(int id) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Scheduling> query = criteriaBuilder.createQuery(Scheduling.class);
        Root<Scheduling> fromScheduling = query.from(Scheduling.class);
        CriteriaQuery<Scheduling> select = query.select(fromScheduling);
        select.where(criteriaBuilder.equal(fromScheduling.get("id"), id));
        return entityManager.createQuery(query).getSingleResult();

    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
