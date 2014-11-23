package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Configuration;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.output.ListenerStat;
import hu.tilos.radio.backend.data.output.StatData;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("api/v1/stat")
public class StatController {

    private static final Logger LOG = LoggerFactory.getLogger(StatController.class);

    @Inject
    EntityManager em;

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    ModelMapper mapper;

    @Inject
    @Configuration(name = "influxdb.url")
    String influxDbUrl;

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

    @Produces("application/json")
    @Security(role = Role.GUEST)
    @GET
    @Path("/listener")
    public List<ListenerStat> getListenerSTat() {
        InfluxDB influxDB = InfluxDBFactory.connect(influxDbUrl, "root", "root");

        List<ListenerStat> result = new ArrayList<>();
        Date now = new Date();
        now.setTime(now.getTime() - 3600000L);
        Date weekAgo = new Date();
        //weekAgo.setTime(now.getTime() - (long) 604800000L);
        weekAgo.setTime(now.getTime() - (long) 30L * 60 * 60 * 24 * 1000);

        List<EpisodeData> episodeList = episodeUtil.getEpisodeData(-1, weekAgo, now);
        for (EpisodeData episode : episodeList) {
            ListenerStat stat = new ListenerStat();
            stat.setEpisode(mapper.map(episode, EpisodeData.class));
            long from = episode.getPlannedFrom().getTime() / 1000;
            long to = episode.getPlannedTo().getTime() / 1000;
            if (episode.getPlannedFrom().equals(episode.getRealFrom())) {
                from += 60 * 15; //15 min
            }

            String query = String.format("select min(expr0),mean(expr0),max(expr0) from calc.icecast where time > %ds and time < %ds", from, to);
            LOG.debug(query);
            List<Serie> series = influxDB.query("tilos", query, TimeUnit.DAYS.SECONDS);

            if (series.size() > 0 && series.get(0).getRows().size() > 0) {
                stat.setMean(convertToInt(series.get(0).getRows().get(0).get("mean")));
                stat.setMax(convertToInt(series.get(0).getRows().get(0).get("max")));
                stat.setMin(convertToInt(series.get(0).getRows().get(0).get("min")));
            } else {
                stat.setMean(0);
                stat.setMax(0);
                stat.setMin(0);
            }
            result.add(stat);
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
