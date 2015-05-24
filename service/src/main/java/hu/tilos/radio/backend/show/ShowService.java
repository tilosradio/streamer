package hu.tilos.radio.backend.show;

import com.mongodb.*;
import hu.radio.tilos.model.type.ShowStatus;
import hu.tilos.radio.backend.ObjectValidator;
import hu.tilos.radio.backend.contribution.ShowContribution;
import hu.tilos.radio.backend.converters.SchedulingTextUtil;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.SchedulingSimple;

import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.mix.MixSimple;
import hu.tilos.radio.backend.util.AvatarLocator;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.*;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;

public class ShowService {

    private static Logger LOG = LoggerFactory.getLogger(ShowService.class);

    private final SchedulingTextUtil schedulingTextUtil = new SchedulingTextUtil();

    @Inject
    ObjectValidator validator;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    AvatarLocator avatarLocator;

    @Inject
    private DozerBeanMapper mapper;

    @Inject
    private DB db;

    public List<ShowSimple> list(@QueryParam("status") String status) {
        BasicDBObject criteria = new BasicDBObject();

        //FIXME
        if (!"all".equals(status)) {
            criteria.put("status", ShowStatus.ACTIVE.ordinal());
        }
        DBCursor selectedShows = db.getCollection("show").find(criteria).sort(new BasicDBObject("name", 1));

        List<ShowSimple> mappedShows = new ArrayList<>();
        for (DBObject show : selectedShows) {
            mappedShows.add(mapper.map(show, ShowSimple.class));
        }
        Collections.sort(mappedShows, new Comparator<ShowSimple>() {
            @Override
            public int compare(ShowSimple s1, ShowSimple s2) {
                return s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase());
            }
        });
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


    public UpdateResponse update(@PathParam("alias") String alias, ShowToSave showToSave) {
        validator.validate(showToSave);
        DBObject show = findShow(alias);
        mapper.map(showToSave, show);
        db.getCollection("show").update(aliasOrId(alias), show);
        return new UpdateResponse(true);

    }

    private DBObject findShow(String alias) {
        return db.getCollection("show").findOne(aliasOrId(alias));
    }


    public CreateResponse create(ShowToSave objectToSave) {
        validator.validate(objectToSave);
        DBObject newObject = mapper.map(objectToSave, BasicDBObject.class);
        newObject.put("alias", objectToSave.getAlias());
        db.getCollection("show").insert(newObject);
        return new CreateResponse(((ObjectId) newObject.get("_id")).toHexString());
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
