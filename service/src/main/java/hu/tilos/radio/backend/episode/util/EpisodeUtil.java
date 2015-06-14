package hu.tilos.radio.backend.episode.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBRef;
import hu.tilos.radio.backend.bookmark.BookmarkData;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.text.TextData;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    @Inject
    DB db;

    public List<EpisodeData> getEpisodeData(String showIdOrAlias, Date from, Date to) {
        List<EpisodeData> merged = merger.merge(
                persistentProvider.listEpisode(showIdOrAlias, from, to),
                scheduledProvider.listEpisode(showIdOrAlias, from, to),
                extraProvider.listEpisode(from, to)
        );

        fillTheBookmarks(from, to, merged);
        merged = filterToShow(showIdOrAlias, merged);
        persistEpisodeFromThePast(merged);
        merged = episodeTextFromBookmark(merged);
        for (EpisodeData episode : merged) {
            linkGenerator(episode);
        }
        return merged;
    }

    private void fillTheBookmarks(Date from, Date to, List<EpisodeData> merged) {
        BasicDBObject query = new BasicDBObject();
        query.put("from", new BasicDBObject("$lt", to));
        query.put("to", new BasicDBObject("$gt", from));
        List<BookmarkData> bookmarks = db.getCollection("bookmark").find(query).toArray().stream().map(dbOject -> {
            BookmarkData d = new BookmarkData();
            d.setFrom((Date) dbOject.get("from"));
            d.setTo((Date) dbOject.get("to"));
            d.setTitle((String) dbOject.get("title"));
            return d;
        }).collect(Collectors.toList());

        bookmarks.stream().forEach(bookmark -> addBookmarkTo(bookmark, merged));

    }

    private void addBookmarkTo(BookmarkData bookmark, List<EpisodeData> merged) {
        Optional<EpisodeData> episode = merged.stream().max((e1, e2) -> getIntersection(e1, bookmark).compareTo(getIntersection(e2, bookmark)));
        if (episode.isPresent()) {
            EpisodeData bestEpisode = episode.get();
            long intersection = getIntersection(bestEpisode, bookmark);
            long bookmarkDuration = (bookmark.getTo().getTime() - bookmark.getFrom().getTime()) / 1000;
            if (intersection * 2 > bookmarkDuration) {
                bestEpisode.getBookmarks().add(bookmark);
            }
        }

    }

    private Long getIntersection(EpisodeData episodeData, BookmarkData bookmark) {
        if (episodeData.getRealTo().after(bookmark.getFrom()) && bookmark.getTo().after(episodeData.getRealFrom())) {
            return (Math.min(episodeData.getRealTo().getTime(), bookmark.getTo().getTime()) - Math.max(episodeData.getRealFrom().getTime(), bookmark.getFrom().getTime())) / 1000;
        } else {
            return Long.valueOf(0);
        }
    }


    private boolean persistEpisodeFromThePast(List<EpisodeData> merged) {
        boolean persisted = false;
        for (EpisodeData episode : merged) {
            if (!episode.isPersistent() && episode.getPlannedFrom().getTime() < new Date().getTime()) {
                generateEpisode(episode);
            }
        }
        return persisted;
    }

    private void generateEpisode(EpisodeData episode) {
        BasicDBObject newMongoOBject = new BasicDBObject();
        newMongoOBject.put("created", new Date());
        newMongoOBject.put("plannedFrom", episode.getPlannedFrom());
        newMongoOBject.put("plannedTo", episode.getPlannedTo());
        newMongoOBject.put("realFrom", episode.getRealFrom());
        newMongoOBject.put("realTo", episode.getRealTo());

        BasicDBObject show = new BasicDBObject();
        show.put("alias", episode.getShow().getAlias());
        show.put("name", episode.getShow().getName());
        show.put("ref", new DBRef(db, "show", episode.getShow().getId()));

        episode.setPersistent(true);

        newMongoOBject.put("show", show);

        db.getCollection("episode").insert(newMongoOBject);
    }

    private List<EpisodeData> episodeTextFromBookmark(List<EpisodeData> original) {
        return original.stream().map(episodeData -> {
                    if (episodeData.getText() == null && episodeData.getBookmarks().size() > 0) {
                        TextData text = new TextData();
                        BookmarkData bookmark = episodeData.getBookmarks().iterator().next();
                        text.setTitle(bookmark.getTitle());
                        episodeData.setText(text);
                        episodeData.setOriginal(false);
                        episodeData.setRealFrom(bookmark.getFrom());
                        episodeData.setRealTo(bookmark.getTo());
                    }
                    return episodeData;
                }
        ).collect(Collectors.toList());
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
