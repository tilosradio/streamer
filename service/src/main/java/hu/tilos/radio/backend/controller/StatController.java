package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.output.StatData;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("api/v1/stat")
public class StatController {

    @Inject
    EntityManager em;

    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    @Path("/summary")
    public StatData getSummary() {
        StatData statData = new StatData();
        statData.showCount = (long) em.createQuery("SELECT COUNT(s) FROM Show s WHERE s.status = 1").getSingleResult();
        statData.authorCount = (long) em.createQuery("SELECT COUNT(c) FROM Contribution c WHERE c.show.status = 1").getSingleResult();
        statData.mixCount = (long) em.createQuery("SELECT COUNT(m) FROM Mix m").getSingleResult();
        statData.episodeCount = (long) em.createQuery("SELECT COUNT(e) FROM Episode e WHERE e.text is not null AND e.text.content is not null AND length(e.text.content) > 5 ").getSingleResult();
        return statData;
    }
}
