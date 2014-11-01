package hu.tilos.radio.backend;

import hu.radio.tilos.model.*;
import hu.tilos.radio.backend.converters.SchedulingTextUtil;
import hu.tilos.radio.backend.data.MixResponse;
import hu.tilos.radio.backend.data.types.*;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.*;
import javax.sql.DataSource;
import javax.ws.rs.*;

import java.util.*;

import static org.dozer.loader.api.FieldsMappingOptions.customConverter;

@Path("/api/v1/show")
public class ShowController {

    private static Logger LOG = LoggerFactory.getLogger(ShowController.class);
    private final SchedulingTextUtil schedulingTextUtil = new SchedulingTextUtil();

    @Inject
    private EntityManager entityManager;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    private ModelMapper modelMapper;

    /**
     * Detailed information about one radioshow.
     *
     * Integer based if also could be used as an alias.
     *
     * @param alias Alias of the radioshow (eg. 3-utas)
     * @return
     */
    @Produces("application/json")
    @Path("/{alias}")
    @Security(role = Role.GUEST)
    @GET
    public ShowDetailed get(@PathParam("alias") String alias) {
        Show show = null;
        if (!alias.matches("\\d+")) {
            show = entityManager.createQuery("SELECT s FROM Show s " +
                    "LEFT JOIN FETCH s.mixes " +
                    "LEFT JOIN FETCH s.contributors " +
                    "LEFT JOIN FETCH s.schedulings " +
                    "WHERE s.alias=:alias", Show.class).setParameter("alias", alias).getSingleResult();
        } else {
            show = entityManager.createQuery("SELECT s FROM Show s " +
                    "LEFT JOIN FETCH s.mixes " +
                    "LEFT JOIN FETCH s.contributors " +
                    "LEFT JOIN FETCH s.schedulings " +
                    "WHERE s.id=:id", Show.class).setParameter("id", Integer.parseInt(alias)).getSingleResult();
        }


        ShowDetailed detailed = modelMapper.map(show, ShowDetailed.class);


        Collections.sort(detailed.getMixes(), new Comparator<MixSimple>() {

            @Override
            public int compare(MixSimple mixSimple, MixSimple mixSimple2) {
                return mixSimple.getTitle().compareTo(mixSimple2.getTitle());
            }
        });

        Collections.sort(detailed.getContributors(), new Comparator<ShowContribution>() {

            @Override
            public int compare(ShowContribution contribution, ShowContribution contribution2) {
                return contribution.getAuthor().getName().compareTo(contribution2.getAuthor().getName());
            }
        });

        Date now = new Date();
        for (SchedulingSimple ss : detailed.getSchedulings()) {
            if (ss.getValidFrom().compareTo(now) < 0 && ss.getValidTo().compareTo(now) > 0)
                ss.setText(schedulingTextUtil.create(ss));
        }


        Long o = (Long) entityManager.createQuery("SELECT count(m) FROM Mix m where m.show = :show").setParameter("show", show).getSingleResult();
        detailed.getStats().mixCount = o.intValue();

        return detailed;

    }

    @GET
    @Path("/{show}/episodes")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    public List<EpisodeData> listEpisodes(@PathParam("show") String showAlias, @QueryParam("start") long from, @QueryParam("end") long to) {
        Date fromDate = new Date();
        fromDate.setTime(from);
        Date toDate = new Date();
        toDate.setTime(to);
        int showId;
        showId = Integer.parseInt(showAlias);
        //todo on parse error
        //Show show = (Show) entityManager.createQuery("SELECT s FROM Show s WHERE s.alias = :alias").setParameter("alias",showAlias).getSingleResult();
        return episodeUtil.getEpisodeData(showId, fromDate, toDate);

    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
