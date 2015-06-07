package hu.tilos.radio.backend.episode.util;

import hu.tilos.radio.backend.episode.EpisodeData;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named
public class EpisodeUtil {

    public static SimpleDateFormat YYYYMMDD = DateFormatUtil.create("yyyyMMdd");

    public static SimpleDateFormat HHMMSS = DateFormatUtil.create("HHmmss");

    public static SimpleDateFormat YYYY_MM_DD = DateFormatUtil.create("yyyy'/'MM'/'dd");

    @Inject
    protected PersistentEpisodeProvider persistentProvider;

    @Inject
    private ScheduledEpisodeProvider scheduledProvider;

    @Inject
    private ExtraEpisodeProvider extraProvider;

    @Inject
    private Merger merger = new Merger();

    public List<EpisodeData> getEpisodeData(String showIdOrAlias, Date from, Date to) {
        List<EpisodeData> merged = merger.merge(
                persistentProvider.listEpisode(showIdOrAlias, from, to),
                scheduledProvider.listEpisode(showIdOrAlias, from, to),
                extraProvider.listEpisode(from, to)
        );
        for (EpisodeData episode : merged) {
            linkGenerator(episode);
        }
        merged = filterToShow(showIdOrAlias, merged);
        return merged;
    }

    private List<EpisodeData> filterToShow(String showIdOrAlias, List<EpisodeData> original) {
        if (showIdOrAlias != null) {
            return original.stream().filter(episodeData ->
                            episodeData.getShow() != null &&
                                    (
                                            showIdOrAlias.equals(episodeData.getShow().getAlias())
                                                    || showIdOrAlias.equals(episodeData.getShow().getId())
                                    )
            ).collect(Collectors.toList());
        } else {
            return original;
        }
    }

    public static EpisodeData linkGenerator(EpisodeData episode) {
        if (episode.getRealTo().compareTo(new Date()) < 0) {
            episode.setM3uUrl("http://tilos.hu/mp3/tilos-" +
                    YYYYMMDD.format(episode.getRealFrom()) +
                    "-" +
                    HHMMSS.format(episode.getRealFrom()) +
                    "-" +
                    HHMMSS.format(episode.getRealTo()) + ".m3u");
        }
        return episode;
    }
}
