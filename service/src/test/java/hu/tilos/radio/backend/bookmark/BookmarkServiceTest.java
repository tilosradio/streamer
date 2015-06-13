package hu.tilos.radio.backend.bookmark;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.episode.util.DateFormatUtil;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.dozer.DozerBeanMapper;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

public class BookmarkServiceTest{

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Inject
    BookmarkService service;

    @Inject
    DozerBeanMapper mapper;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void testGet() throws Exception {
        //given
        String episodeId = loadTo(fongoRule, "episode", "episode-episode2.json");

        BookmarkToSave bts = new BookmarkToSave();
        bts.setFrom(DateFormatUtil.YYYY_MM_DD_HHMM.parse("2014-10-12 10:00"));
        bts.setTo(DateFormatUtil.YYYY_MM_DD_HHMM.parse("2014-10-12 11:00"));
        bts.setEpisodeRef(episodeId);
        bts.setTitle("asd");

        //when
        service.create(session, episodeId, bts);

        //then
        DBObject episode = fongoRule.getDB().getCollection("episode").findOne(new BasicDBObject("_id", new ObjectId(episodeId)));
        Assert.assertNotNull(episode);
        Assert.assertNotNull(episode.get("bookmarks"));
    }
}