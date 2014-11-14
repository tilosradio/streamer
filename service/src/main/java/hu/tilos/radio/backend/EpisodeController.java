package hu.tilos.radio.backend;

import hu.radio.tilos.model.Episode;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.Show;
import hu.radio.tilos.model.Tag;
import hu.tilos.radio.backend.converters.TagUtil;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.data.UpdateResponse;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Path("/api/v1/episode")
public class EpisodeController {

    private static final Logger LOG = LoggerFactory.getLogger(EpisodeController.class);

    @Inject
    ModelMapper modelMapper;

    @Inject
    private EntityManager entityManager;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    Session session;

    @Inject
    TagUtil tagUtil;

    @GET
    @Path("/{id}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public EpisodeData get(@PathParam("id") int i) {
        EpisodeData r = modelMapper.map(entityManager.find(Episode.class, i), EpisodeData.class);
        return r;
    }

    @GET
    @Path("/{show}/{year}/{month}/{day}")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public EpisodeData getByDate(@PathParam("show") String showAlias, @PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) {
        Show show = (Show) entityManager.createQuery("SELECT s FROM Show s WHERE s.alias = :alias").setParameter("alias", showAlias).getSingleResult();
        List<EpisodeData> episodeData = episodeUtil.getEpisodeData(show.getId(), new Date(year - 1900, month - 1, day), new Date(year - 1900, month - 1, day, 23, 59, 59));
        if (episodeData.size() == 0) {
            //todo, error handling
            throw new IllegalArgumentException("Can't find the appropriate episode");
        } else {
            return episodeData.get(0);
        }
    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.AUTHOR)
    @POST
    @Transactional
    public CreateResponse create(EpisodeData data) {

        Episode entity = modelMapper.map(data, Episode.class);

        if (entity.getText() != null) {
            entity.getText().setAlias("");
            entity.getText().setFormat("default");
            entity.getText().setType("episode");
            entityManager.persist(entity.getText());
            entityManager.flush();
        }
        if (entity.getRealFrom() == null) {
            entity.setRealFrom(entity.getPlannedFrom());
        }
        if (entity.getRealTo() == null) {
            entity.setRealTo(entity.getPlannedTo());
        }
        updateTags(entity);
        entityManager.persist(entity);
        entityManager.flush();
        return new CreateResponse(entity.getId());

    }

    /**
     * @exclude
     */
    @Produces("application/json")
    @Security(role = Role.AUTHOR)
    @Transactional
    @PUT
    @Path("/{id}")
    public UpdateResponse update(@PathParam("id") int id, EpisodeData inputData) {

        Episode entity = entityManager.find(Episode.class, id);

        modelMapper.map(inputData, entity);

        if (entity.getText() != null) {
            entity.getText().setAlias("");
            entity.getText().setFormat("default");
            entity.getText().setType("episode");
            entityManager.persist(entity.getText());
            entityManager.flush();
        }
        updateTags(entity);
        entityManager.persist(entity);
        entityManager.flush();

        return new UpdateResponse(entity.getId());
    }


    public void updateTags(Episode episode) {
        if (episode.getText() != null && episode.getText().getContent() != null) {
            Set<Tag> newTags = tagUtil.getTags(episode.getText().getContent());
            tagUtil.updateTags(entityManager, episode.getText(), newTags);
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

}
