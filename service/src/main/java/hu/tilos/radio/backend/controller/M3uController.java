package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.FeedRenderer;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import hu.tilos.radio.backend.util.Days;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Generate various m3u feeds.
 */
@Path("/api/v1/m3u")
public class M3uController {

    @Inject
    private EpisodeUtil episodeUtil;

    private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy'.'MM'.'dd");

    private static final SimpleDateFormat MM_DD = new SimpleDateFormat("MM'.'dd");

    private static final SimpleDateFormat HH_MM = new SimpleDateFormat("HH':'mm");

    @GET
    @Path("lastweek")
    @Produces("audio/x-mpegurl; charset=iso-8859-2")
    @Security(role = Role.GUEST)
    public Response lastWeek(@QueryParam("stream") String query) {
        if (query == null) {
            query = "/tilos";
        }
        query = query.replace(".m3u", "");
        Date now = new Date();
        Date weekAgo = new Date();
        weekAgo.setTime(now.getTime() - (long) 604800000L);
        List<EpisodeData> episodes = episodeUtil.getEpisodeData(-1, weekAgo, now);

        Collections.sort(episodes, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData o1, EpisodeData o2) {
                return -1 * o1.getRealFrom().compareTo(o2.getRealFrom());
            }
        });

        episodes.remove(0);

        StringBuilder result = new StringBuilder();
        result.append("#EXTM3U\n");
        result.append("#EXTINF:-1, Tilos Rádió - élő adás (256kb/s) \n");
        result.append("http://stream.tilos.hu" + query + "\n");
        result.append("#EXTINF:-1, Tilos Rádió - [CSAKASZAVAK] Szöveges archívum \n");
        result.append("http://stream.tilos.hu/csakaszavak.ogg\n");
        result.append("#EXTINF:-1, Tilos Rádió - [CSAKAZENE] Zenés archívum\n");
        result.append("http://stream.tilos.hu/csakazene.ogg\n");
        for (EpisodeData episode : episodes) {
            String artist = episode.getShow().getName().replaceAll("-", ", ");

            Date start = episode.getPlannedFrom();

            String title = "[" + MM_DD.format(start) + " - " + Days.values()[start.getDay()].getHungarian() + " " + HH_MM.format(start) + "]";
            if (episode.getText() != null) {
                title += " " + episode.getText().getTitle();
            } else {
                title += " adás archívum";
            }
            result.append("#EXTINF:-1, " + artist + " - " + title + "\n");
            result.append(FeedRenderer.createDownloadURI(episode) + "\n");
        }
        return Response.ok(result.toString()).build();
    }

    public EpisodeUtil getEpisodeUtil() {
        return episodeUtil;
    }

    public void setEpisodeUtil(EpisodeUtil episodeUtil) {
        this.episodeUtil = episodeUtil;
    }
}
