package hu.tilos.radio.backend.episode.util;

import com.mongodb.*;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.data.types.SchedulingSimple;
import hu.tilos.radio.backend.data.types.ShowSimple;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import java.util.*;

import static hu.tilos.radio.backend.MongoUtil.aliasOrId;


/**
 * Returns with the persisted episode records.
 */
public class ScheduledEpisodeProvider {

    @Inject
    private DozerBeanMapper modelMapper;

    @Inject
    private DB db;

    public List<EpisodeData> listEpisode(String showIdOrAlias, final Date from, final Date to) {
        BasicDBObject query = new BasicDBObject();
        query.put("schedulings.validFrom", new BasicDBObject("$lt", to));
        query.put("schedulings.validTo", new BasicDBObject("$gt", from));


        if (showIdOrAlias != null) {
            BasicDBObject q = aliasOrId(showIdOrAlias);
            String key = q.keySet().iterator().next();
            query.put(key, q.get(key));
        } else {
            query.put("status", 1);
        }

        DBCursor shows = db.getCollection("show").find(query);


        List<EpisodeData> result = new ArrayList<>();
        for (DBObject show : shows) {
            ShowSimple simpleShow = modelMapper.map(show, ShowSimple.class);
            BasicDBList schedulings = (BasicDBList) show.get("schedulings");
            if (schedulings != null) {
                for (int i = 0; i < schedulings.size(); i++) {
                    SchedulingSimple s = modelMapper.map(schedulings.get(i), SchedulingSimple.class);
                    result.addAll(calculateEpisodes(s, simpleShow, from, to));
                }
            }

        }

        return result;

    }

    private List<EpisodeData> calculateEpisodes(SchedulingSimple s, ShowSimple show, Date from, Date to) {

        Calendar toCalendar = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        Calendar scheduledUntil = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        toCalendar.setTime(to);
        scheduledUntil.setTime(s.getValidTo());

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("CET"));
        c.set(from.getYear() + 1900, from.getMonth(), from.getDate(), s.getHourFrom(), s.getMinFrom(), 0);
        int offset = c.get(Calendar.DAY_OF_WEEK) - 2;
        if (offset < 0) {
            offset += 7;
        }
        c.add(Calendar.DAY_OF_MONTH, -1 * offset + s.getWeekDay());
        c.set(Calendar.MILLISECOND, 0);

        List<EpisodeData> result = new ArrayList<>();
        while (c.compareTo(toCalendar) < 0 && c.compareTo(scheduledUntil) < 0) {
            if (isValidDate(c, s, from, to)) {
                //create episode from scheduling
                EpisodeData d = new EpisodeData();
                d.setPlannedFrom(c.getTime());
                d.setRealFrom(d.getPlannedFrom());

                Date exactToDate = new Date();
                exactToDate.setTime(d.getPlannedFrom().getTime() + (s.getDuration()) * 60 * 1000);
                d.setPlannedTo(exactToDate);


                Date estimatedToDate = new Date();
                estimatedToDate.setTime(d.getPlannedFrom().getTime() + (s.getDuration() + 30) * 60 * 1000);
                d.setRealTo(estimatedToDate);

                d.setPersistent(false);
                d.setShow(show);
                result.add(d);
            }
            c.add(Calendar.DAY_OF_MONTH, 7);
        }
        return result;


    }

    protected boolean isValidDate(Calendar c, SchedulingSimple s, Date from, Date to) {
        if (s.getWeekType() > 1) {
            int weekNo = (int) Math.floor((c.getTime().getTime() - s.getBase().getTime()) / (7000l * 60 * 60 * 24));
            if (weekNo % s.getWeekType() != 0) {
                return false;
            }
        }
        Long realTime = c.getTime().getTime();
        Long toTime = to.getTime();
        Long fromTime = from.getTime();
        Long validFromTime = s.getValidFrom().getTime();
        Long validToTime = s.getValidTo().getTime();

        if (realTime.compareTo(fromTime) >= 0 && realTime.compareTo(toTime) < 0 && realTime.compareTo(validFromTime) >= 0 && realTime.compareTo(validToTime) < 0) {
            return true;
        }
        return false;
    }

    private Date weekStart(Date validFrom) {
        return null;
    }

    public void setDb(DB db) {
        this.db = db;
    }
}
