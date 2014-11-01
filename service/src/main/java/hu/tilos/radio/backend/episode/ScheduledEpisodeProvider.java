package hu.tilos.radio.backend.episode;

import hu.radio.tilos.model.Scheduling;
import hu.radio.tilos.model.Show;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.data.types.ShowSimple;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.util.*;
import java.util.Date;


/**
 * Returns with the persisted episode records.
 */
public class ScheduledEpisodeProvider {

    @Inject
    private ModelMapper modelMapper;

    @Inject
    private EntityManager entityManager;

    public List<EpisodeData> listEpisode(int showId, final Date from, final Date to) {


        String query = "SELECT s from Scheduling s WHERE s.validFrom < :end AND s.validTo > :start";
        if (showId > 0) {
            query += " AND s.show.id = :showId";
        } else {
            query += " AND s.show.status = 1";
        }
        Query q = entityManager.createQuery(query);
        q.setParameter("start", from);
        q.setParameter("end", to);
        if (showId > 0) {
            q.setParameter("showId", showId);
        }
        List<Scheduling> schedulings = q.getResultList();

        List<EpisodeData> result = new ArrayList<>();
        for (Scheduling s : schedulings) {
            result.addAll(calculateEpisodes(s, s.getShow(), from, to));

        }

        return result;

    }

    private List<EpisodeData> calculateEpisodes(Scheduling s, Show show, Date from, Date to) {

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
                Date dateToSet = new Date();
                dateToSet.setTime(d.getPlannedFrom().getTime() + (s.getDuration() + 30) * 60 * 1000);
                d.setPlannedTo(dateToSet);
                d.setRealFrom(d.getPlannedFrom());
                d.setRealTo(d.getPlannedTo());
                d.setPersistent(false);
                d.setShow(modelMapper.map(show, ShowSimple.class));
                result.add(d);
            }
            c.add(Calendar.DAY_OF_MONTH, 7);
        }
        return result;


    }

    protected boolean isValidDate(Calendar c, Scheduling s, Date from, Date to) {
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

}
