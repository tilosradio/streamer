package hu.tilos.radio.backend.m3u;

import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.DateFormatUtil;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.feed.FeedRenderer;
import hu.tilos.radio.backend.util.Days;
import hu.tilos.radio.backend.util.LocaleUtil;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Generate various m3u feeds.
 */
public class M3uService {

    @Inject
    private EpisodeUtil episodeUtil;


    public String lastWeek(@QueryParam("stream") String query) {
        if (query == null) {
            query = "/tilos";
        }
        query = query.replace(".m3u", "");
        Date now = new Date();
        Date weekAgo = new Date();
        weekAgo.setTime(now.getTime() - (long) 604800000L);
        List<EpisodeData> episodes = episodeUtil.getEpisodeData(null, weekAgo, now);

        Collections.sort(episodes, new Comparator<EpisodeData>() {
            @Override
            public int compare(EpisodeData o1, EpisodeData o2) {
                return -1 * o1.getRealFrom().compareTo(o2.getRealFrom());
            }
        });

        episodes.remove(0);

        StringBuilder result = new StringBuilder();
        result.append("#EXTM3U\n");
        result.append("#EXTINF:-1, Tilos Rádió - élő adás\n");
        result.append("http://stream.tilos.hu" + query + "\n");
        for (EpisodeData episode : episodes) {
            String artist = episode.getShow().getName().replaceAll("-", ", ");

            Date start = episode.getPlannedFrom();

            String title = "[" +  DateFormatUtil.HH_MM.format(start) + " - " + Days.values()[start.getDay()].getHungarian() + " " + DateFormatUtil.HH_MM.format(start) + "]";
            if (episode.getText() != null) {
                title += " " + episode.getText().getTitle();
            } else {
                title += " adás archívum";
            }
            result.append("#EXTINF:-1, " + artist + " - " + title + "\n");
            result.append(FeedRenderer.createDownloadURI(episode) + "\n");
        }
        return result.toString();
    }

    public EpisodeUtil getEpisodeUtil() {
        return episodeUtil;
    }

    public void setEpisodeUtil(EpisodeUtil episodeUtil) {
        this.episodeUtil = episodeUtil;
    }
}
