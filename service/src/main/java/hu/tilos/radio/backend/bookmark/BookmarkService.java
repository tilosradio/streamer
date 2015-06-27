package hu.tilos.radio.backend.bookmark;

import com.mongodb.*;
import hu.tilos.radio.backend.Session;
import hu.tilos.radio.backend.data.response.CreateResponse;
import org.bson.types.ObjectId;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;

public class BookmarkService {

    private static final Logger LOG = LoggerFactory.getLogger(BookmarkService.class);

    @Inject
    DozerBeanMapper modelMapper;

    @Inject
    DB db;

    public void update() {

    }

    public void setDb(DB db) {
        this.db = db;
    }

    public CreateResponse create(Session session, String episodeId, BookmarkToSave bookmarkToSave) {
        BasicDBObject episodeSelector = new BasicDBObject("_id", new ObjectId(episodeId));
        DBObject episode = db.getCollection("episode").findOne(episodeSelector);
        if (episode == null) {
            throw new IllegalArgumentException("No such episode" + episodeId);
        }
        BasicDBObject bookmark = new BasicDBObject();
        bookmark.put("from", bookmarkToSave.getFrom());
        bookmark.put("to", bookmarkToSave.getTo());
        bookmark.put("title", bookmarkToSave.getTitle());
        bookmark.put("created", new Date());

        BasicDBObject creator = new BasicDBObject();
        creator.put("ref", new DBRef(db, "user", new ObjectId(session.getCurrentUser().getId())));
        creator.put("username", session.getCurrentUser().getUsername());
        bookmark.put("creator", creator);
        if (episode.get("bookmarks") == null) {
            episode.put("bookmarks", new BasicDBList());
        }
        ((BasicDBList) episode.get("bookmarks")).add(bookmark);
        db.getCollection("episode").update(episodeSelector, episode, true, false);
        return new CreateResponse(true);
    }
}
