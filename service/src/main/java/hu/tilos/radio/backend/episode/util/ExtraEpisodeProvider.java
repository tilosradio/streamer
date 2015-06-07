package hu.tilos.radio.backend.episode.util;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.episode.EpisodeData;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Query the EXTRA episodes for a specific time.
 */
public class ExtraEpisodeProvider {


    @Inject
    private DozerBeanMapper mapper;

    @Inject
    private DB db;


    public List<EpisodeData> listEpisode(Date from, Date to) {


        BasicDBObject query = new BasicDBObject();

        query.put("plannedFrom", new BasicDBObject("$lt", to));
        query.put("plannedTo", new BasicDBObject("$gt", from));
        query.put("extra", true);

        DBCursor episodes = db.getCollection("episode").find(query);

        List<EpisodeData> result = new ArrayList<>();
        for (DBObject e : episodes) {
            EpisodeData d = mapper.map(e, EpisodeData.class);
            d.setPersistent(true);
            result.add(d);
        }

        return result;

    }

    public void setDb(DB db) {
        this.db = db;
    }
}
