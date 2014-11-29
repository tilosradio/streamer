package hu.tilos.radio.backend.episode;

import hu.tilos.radio.backend.data.types.EpisodeData;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Named
public class EpisodeUtil {

    public static SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");

    public static SimpleDateFormat HHMMSS = new SimpleDateFormat("HHmmss");

    public static SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy'/'MM'/'dd");

    @Inject
    protected PersistentEpisodeProvider persistentProvider;

    @Inject
    private ScheduledEpisodeProvider scheduledProvider;

    @Inject
    private Merger merger = new Merger();

    public List<EpisodeData> getEpisodeData(String showIdOrAlias, Date from, Date to) {
        List<EpisodeData> merged = merger.merge(persistentProvider.listEpisode(showIdOrAlias, from, to), scheduledProvider.listEpisode(showIdOrAlias, from, to));
        for (EpisodeData episode : merged){
            linkGenerator(episode);
        }
        return merged;
    }

    public PersistentEpisodeProvider getPersistentProvider() {
        return persistentProvider;
    }

    public void setPersistentProvider(PersistentEpisodeProvider persistentProvider) {
        this.persistentProvider = persistentProvider;
    }

    public ScheduledEpisodeProvider getScheduledProvider() {
        return scheduledProvider;
    }

    public void setScheduledProvider(ScheduledEpisodeProvider scheduledProvider) {
        this.scheduledProvider = scheduledProvider;
    }

    public void linkGenerator(EpisodeData episode) {
        if (episode.getRealTo().compareTo(new Date()) < 0) {
            episode.setM3uUrl("http://tilos.hu/mp3/tilos-" +
                    YYYYMMDD.format(episode.getRealFrom()) +
                    "-" +
                    HHMMSS.format(episode.getRealFrom()) +
                    "-" +
                    HHMMSS.format(episode.getRealTo()) + ".m3u");
        }
    }
}
