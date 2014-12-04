package hu.tilos.radio.backend.controller;

import com.mongodb.*;
import hu.radio.tilos.model.Role;
import hu.radio.tilos.model.type.ShowStatus;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.converters.SchedulingTextUtil;
import hu.tilos.radio.backend.data.input.ShowToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.*;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import java.util.*;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

@Path("/api/v1/show")
public class ShowController {

    private static Logger LOG = LoggerFactory.getLogger(ShowController.class);

    private final SchedulingTextUtil schedulingTextUtil = new SchedulingTextUtil();

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    Session session;

    @Inject
    private DozerBeanMapper mapper;

    @Inject
    private DB db;

    @Inject
    AvatarLocator avatarLocator;

    @Produces("application/json")
    @Path("/")
    @Security(role = Role.GUEST)
    @GET
    public List<ShowSimple> list(@QueryParam("status") String status) {
        BasicDBObject criteria = new BasicDBObject();

        //FIXME
        if (!"all".equals("showStatus")) {
            criteria.put("status", ShowStatus.ACTIVE.ordinal());
        }
        DBCursor selectedShows = db.getCollection("show").find(criteria).sort(new BasicDBObject("name", 1));

        List<ShowSimple> mappedShows = new ArrayList<>();
        for (DBObject show : selectedShows) {
            mappedShows.add(mapper.map(show, ShowSimple.class));
        }
        return mappedShows;

    }

    private ShowStatus processStatus(String status) {
        if (status == null) {
            return ShowStatus.ACTIVE;
        } else if (status.equals("all")) {
            return null;
        } else {
            return ShowStatus.valueOf(status.toUpperCase());
        }
    }

    /**
     * Detailed information about one radioshow.
     * <p/>
     * Integer based if also could be used as an alias.
     *
     * @param alias Alias of the radioshow (eg. 3-utas)
     * @return
     */
    @Produces("application/json")
    @Path("/{alias}")
    @Security(role = Role.GUEST)
    @GET
    @Transactional
    public ShowDetailed get(@PathParam("alias") String alias) {
        DBObject one = db.getCollection("show").findOne(aliasOrId(alias));
        ShowDetailed detailed = mapper.map(one, ShowDetailed.class);

        Collections.sort(detailed.getMixes(), new Comparator<MixSimple>() {

            @Override
            public int compare(MixSimple mixSimple, MixSimple mixSimple2) {
                return mixSimple.getTitle().compareTo(mixSimple2.getTitle());
            }
        });

        Date now = new Date();
        for (SchedulingSimple ss : detailed.getSchedulings()) {
            if (ss.getValidFrom().compareTo(now) < 0 && ss.getValidTo().compareTo(now) > 0)
                ss.setText(schedulingTextUtil.create(ss));
        }
        if (detailed.getContributors() != null) {
            for (ShowContribution contributor : detailed.getContributors()) {
                if (contributor.getAuthor() != null) {
                    avatarLocator.locateAvatar(contributor.getAuthor());
                }
            }
        }
        long mixCount = db.getCollection("mix").count(new BasicDBObject("show.ref", new DBRef(db, "show", one.get("_id").toString())));
        detailed.getStats().mixCount = (int) mixCount;
        return detailed;

    }


    @GET
    @Path("/{show}/episodes")
    @Security(role = Role.GUEST)
    @Produces("application/json")
    @Transactional
    public List<EpisodeData> listEpisodes(@PathParam("show") String showAlias, @QueryParam("start") long from, @QueryParam("end") long to) {
        Date fromDate = new Date();
        fromDate.setTime(from);
        Date toDate = new Date();
        toDate.setTime(to);
        List<EpisodeData> episodeData = episodeUtil.getEpisodeData(showAlias, fromDate, toDate);
        Collections.sort(episodeData, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData e1, EpisodeData e2) {
                return e1.getPlannedFrom().compareTo(e2.getPlannedFrom()) * -1;
            }
        });
        return episodeData;

    }


    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/{alias}")
    @Security(role = Role.AUTHOR)
    @PUT
    @Transactional
    public UpdateResponse update(@PathParam("alias") String alias, ShowToSave showToSave) {
        DBObject show = findShow(alias);
        checkPermission(show, session.getCurrentUser());
        mapper.map(showToSave, show);
        db.getCollection("show").update(aliasOrId(alias), show);
        return new UpdateResponse(true);

    }

    private DBObject findShow(String alias) {
        return db.getCollection("show").findOne(aliasOrId(alias));
    }

    protected void checkPermission(DBObject show, UserDetailed currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
//      FIXME
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Contribution> query = criteriaBuilder.createQuery(Contribution.class);
//        Root<Contribution> fromContribution = query.from(Contribution.class);
//        query.where(criteriaBuilder.equal(fromContribution.get("author").get("user").get("id"), currentUser.getId()));
//        List<Contribution> contributions = entityManager.createQuery(query).getResultList();
//
//        for (hu.radio.tilos.model.Contribution contribution : contributions) {
//            if (contribution.getShow().getId() == ((Integer) show.get("id")).intValue()) {
//                return;
//            }
//        }
        throw new IllegalArgumentException("No permission to modify");
    }

    /**
     * @exclude
     */
    @Produces("application/json")
    @Path("/")
    @Security(role = Role.ADMIN)
    @POST
    @Transactional
    public CreateResponse create(ShowToSave objectToSave) {
        DBObject newObject = mapper.map(objectToSave, BasicDBObject.class);
        db.getCollection("show").insert(newObject);
        return new CreateResponse(((ObjectId) newObject.get("_id")).toHexString());
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
