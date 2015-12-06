package hu.tilos.radio.backend.feed;

import hu.tilos.radio.backend.Configuration;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.episode.util.DateFormatUtil;
import net.anzix.jaxrs.atom.*;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility to create Feed object from Episode list.
 */
public class FeedRenderer {

    private static final SimpleDateFormat YYYY_DOT_MM_DOT_DD = DateFormatUtil.create("yyyy'.'MM'.'dd");

    private static final SimpleDateFormat YYYY_PER_MM_PER_DD = DateFormatUtil.create("yyyy'/'MM'/'dd");

    private static final SimpleDateFormat YYYYMMDD = DateFormatUtil.create("yyyyMMdd");

    private static final SimpleDateFormat HHMMSS = DateFormatUtil.create("HHmmss");

    @Inject
    @Configuration(name = "server.url")
    private String serverUrl;


    public static String createDownloadURI(EpisodeData episode) {
        return "http://tilos.hu/mp3/tilos-" +
                YYYYMMDD.format(episode.getRealFrom()) + "-" +
                HHMMSS.format(episode.getRealFrom()) + "-" +
                HHMMSS.format(episode.getRealTo()) + ".mp3";
    }

    private static Date dateFromEpoch(long realTo) {
        Date d = new Date();
        d.setTime(realTo);
        return d;
    }

    public Feed generateFeed(List<EpisodeData> episodeData, String id) {
        return generateFeed(episodeData, id, false);
    }

    public Feed generateFeed(List<EpisodeData> episodeData, String id, boolean prefixedWithShowName) {
        Feed feed = new Feed();
        feed.setITunesAuthor("Tilos Radio");
        feed.setLanguage("hu");
        try {
            feed.setId(new URI(id));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {


            Person p = new Person();
            p.setEmail("info@tilos.hu");
            p.setName("Tilos Rádió");

            List<Person> authors = new ArrayList();
            authors.add(p);


            for (EpisodeData episode : episodeData) {
                try {
                    Entry e = new Entry();
                    String prefix = prefixedWithShowName ? episode.getShow().getName() + ": " : "";
                    if (episode.getText() != null) {
                        e.setTitle(prefix + episode.getText().getTitle());
                        e.setSummary(new Summary("html", episode.getText().getFormatted()));
                    } else {
                        e.setTitle(prefix + YYYY_DOT_MM_DOT_DD.format(episode.getPlannedFrom()) + " " + "adásnapló");
                        e.setSummary(new Summary("adás archívum"));
                    }

                    e.setITunesSummary(e.getSummary().getContent());

                    e.setITunesDuration((episode.getRealTo().getTime() - episode.getRealFrom().getTime()) / 100);

                    e.setPublished(episode.getRealTo());
                    e.setUpdated(episode.getRealTo());

                    URL url = new URL(serverUrl + "/episode/" + episode.getShow().getAlias() + "/" + YYYY_PER_MM_PER_DD.format(e.getPublished()));

                    e.setId(url.toURI());

                    Link alternate = new Link();
                    alternate.setRel("alternate");
                    alternate.setType(MediaType.TEXT_HTML_TYPE);
                    alternate.setHref(url.toURI());
                    e.getLinks().add(alternate);

                    Link sound = new Link();
                    sound.setType(new MediaType("audio", "mpeg"));
                    sound.setRel("enclosure");
                    sound.setHref(new URI(createDownloadURI(episode)));
                    e.getLinks().add(sound);


                    e.getAuthors().addAll(authors);

                    feed.getEntries().add(e);
                } catch (MalformedURLException e1) {
                    throw new RuntimeException(e1);
                } catch (URISyntaxException e1) {
                    throw new RuntimeException(e1);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //TODO
        }
        return feed;
    }
}
