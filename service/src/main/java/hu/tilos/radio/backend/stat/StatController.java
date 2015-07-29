package hu.tilos.radio.backend.stat;

import com.mongodb.*;
import hu.tilos.radio.backend.data.output.ListenerStat;
import hu.tilos.radio.backend.data.output.StatData;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class StatController {

    private static final Logger LOG = LoggerFactory.getLogger(StatController.class);

    @Inject
    DB db;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    DozerBeanMapper mapper;

    public StatData getSummary() {
        StatData statData = new StatData();
        statData.showCount = db.getCollection("show").count(new BasicDBObject("status", 1));
        //todo show only the active authors
        statData.authorCount = db.getCollection("author").count();
        statData.mixCount = db.getCollection("mix").count();
        statData.episodeCount = db.getCollection("episode").count();
        return statData;
    }

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

        fixUnderscore(fromDate, toDate);

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
                BasicDBObject group = new BasicDBObject().append("_id", null);
                group.append("min", new BasicDBObject("$min", new BasicDBObject("$add", fields)));
                group.append("max", new BasicDBObject("$max", new BasicDBObject("$add", fields)));
                group.append("avg", new BasicDBObject("$avg", new BasicDBObject("$add", fields)));
                pipeline.add(new BasicDBObject("$group", group));
                AggregationOutput stat_icecast = db.getCollection("stat_icecast").aggregate(pipeline);
                for (DBObject o : stat_icecast.results()) {
                    stat.setMax((Integer) o.get("max"));
                    stat.setMin((Integer) o.get("min"));
                    stat.setMean((int) Math.round((Double) o.get("avg")));
                }

                result.add(stat);
            } catch (Exception ex) {
                LOG.error("Can't calculate listening stat for " + episode.getPlannedFrom(), ex);
            }
        }
        return result;
    }

    private void fixUnderscore(Date fromDate, Date toDate) {
        DBCollection icecast = db.getCollection("stat_icecast");
        DBCursor stat_icecast = icecast.find(QueryBuilder.start().put("time").greaterThan(fromDate).lessThan(toDate).get());
        while (stat_icecast.hasNext()) {
            DBObject next = stat_icecast.next();
            boolean update = false;
            update = fixField("tilos_128.mp3", next) || update;
            update = fixField("tilos_32.mp3", next) || update;
            if (update) {
                icecast.update(new BasicDBObject("_id", next.get("_id")), next);
            }
        }
    }

    private boolean fixField(String field, DBObject record) {
        if (record.containsField(field)) {
            record.put(field.replace('.', '_'), record.get(field));
            record.removeField(field);
            return true;
        }
        return false;
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
