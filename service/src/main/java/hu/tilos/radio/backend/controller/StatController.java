package hu.tilos.radio.backend.controller;

import com.mongodb.*;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.output.ListenerStat;
import hu.tilos.radio.backend.data.output.StatData;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Path("api/v1/stat")
public class StatController {

    private static final Logger LOG = LoggerFactory.getLogger(StatController.class);

    @Inject
    DB db;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    DozerBeanMapper mapper;

    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    @Path("/summary")
    public StatData getSummary() {
        StatData statData = new StatData();
        statData.showCount = db.getCollection("show").count(new BasicDBObject("status", 1));
        //todo show only the active authors
        statData.authorCount = db.getCollection("author").count();
        statData.mixCount = db.getCollection("mix").count();
        statData.episodeCount = db.getCollection("episode").count();
        return statData;
    }

    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    @Path("/listener")
    public List<ListenerStat> getListenerSTat(@QueryParam("from") Long fromTimestamp, @QueryParam("to") Long toTimestamp) {

        List<ListenerStat> result = new ArrayList<>();
        Date toDate = new Date();
        Date fromDate = new Date();
        fromDate.setTime(toDate.getTime() - (long) 7L * 60 * 60 * 24 * 1000);
        if (fromTimestamp != null) {
            fromDate.setTime(fromTimestamp);
        }
        if (toTimestamp != null) {
            toDate.setTime(toTimestamp);
        }

        List<EpisodeData> episodeList = episodeUtil.getEpisodeData(null, fromDate, toDate);
        for (EpisodeData episode : episodeList) {
            try {
                ListenerStat stat = new ListenerStat();
                stat.setEpisode(mapper.map(episode, EpisodeData.class));
                long from = episode.getPlannedFrom().getTime();
                if (episode.getPlannedFrom().equals(episode.getRealFrom())) {
                    from += 60 * 15 * 1000; //15 min
                }
                Date episodeFromDate = new Date(from);

                List<DBObject> pipeline = new ArrayList<>();

                DBObject match = new BasicDBObject("$match", QueryBuilder.start().put("time").greaterThan(episodeFromDate).lessThan(episode.getPlannedTo()).get());
                pipeline.add(match);
                BasicDBList fields = new BasicDBList();
                fields.add("$tilos");
                fields.add("$tilos_128_mp3");
                fields.add("$tilos_32_mp3");
                fields.add("$tilos_high_ogg");
                fields.add("$tilos_low_ogg");
                BasicDBObject group = new BasicDBObject().append("_id", null);
                group.append("min", new BasicDBObject("$min", new BasicDBObject("$add", fields)));
                group.append("max", new BasicDBObject("$max", new BasicDBObject("$add", fields)));
                group.append("avg", new BasicDBObject("$avg", new BasicDBObject("$add", fields)));
                pipeline.add(new BasicDBObject("$group", group));
                System.out.println(pipeline);
                AggregationOutput stat_icecast = db.getCollection("stat_icecast").aggregate(pipeline);
                for (DBObject o : stat_icecast.results()) {
                    stat.setMax((Integer) o.get("max"));
                    stat.setMin((Integer) o.get("min"));
                    stat.setMean((int) Math.round((Double) o.get("avg")));
                }

                result.add(stat);
            } catch (Exception ex) {
                LOG.error("Can't calculate listening stat for " + episode.getPlannedFrom());
            }
        }
        return result;
    }

    private int convertToInt(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new IllegalArgumentException("Can't convert " + value.getClass() + " to int");
        }
    }
}
