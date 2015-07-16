package hu.tilos.radio.backend.m3u;

import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.DateFormatUtil;
import hu.tilos.radio.backend.episode.util.EpisodeUtil;
import hu.tilos.radio.backend.feed.FeedRenderer;
import hu.tilos.radio.backend.util.Days;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate various m3u feeds.
 */
public class M3uService {

    @Inject
    private EpisodeUtil episodeUtil;


    public String lastWeek(String streamName, String filterType) {
        if (streamName == null) {
            streamName = "/tilos";
        }
        streamName = streamName.replace(".m3u", "");
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

        episodes = filter(episodes, filterType);



        StringBuilder result = new StringBuilder();
        result.append("#EXTM3U\n");
        String classification = "";
        if (streamName == "/tilos") {
            classification = " - 256k";
        } else if (streamName == "/tilos_128.mp3") {
            classification = " - 128k";
        } else if (streamName == "/tilos_32.mp3") {
            classification = " - mobil";
        }
        result.append("#EXTINF:-1, Tilos Rádió - live" + classification + "\n");
        result.append("http://stream.tilos.hu" + streamName + "\n");
        for (EpisodeData episode : episodes) {
            String artist = episode.getShow().getName().replaceAll("-", ", ");

            Date start = episode.getPlannedFrom();

            String title = "[" + Days.values()[start.getDay()].getHungarian() + " " + DateFormatUtil.HH_MM.format(start) + "]";
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

    private List<EpisodeData> filter(List<EpisodeData> episodes, String filterType) {
        if (filterType == null) {
            return episodes;
        } else {
            return episodes.stream().filter(episodeData -> episodeData.getShow().getType().name().toLowerCase().equals(filterType)).collect(Collectors.toList());
        }
    }

    public EpisodeUtil getEpisodeUtil() {
        return episodeUtil;
    }

    public void setEpisodeUtil(EpisodeUtil episodeUtil) {
        this.episodeUtil = episodeUtil;
    }
}
