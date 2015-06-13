package hu.tilos.radio.backend.episode.util;


import com.mongodb.*;
import hu.tilos.radio.backend.episode.EpisodeData;
import hu.tilos.radio.backend.text.TextData;
import org.dozer.DozerBeanMapper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Returns with the persisted episode records.
 */
public class PersistentEpisodeProvider {


    @Inject
    private DozerBeanMapper mapper;

    @Inject
    private DB db;


    public List<EpisodeData> listEpisode(String showAlias, Date from, Date to) {


        BasicDBObject query = new BasicDBObject();

        query.put("plannedFrom", new BasicDBObject("$lt", to));
        query.put("plannedTo", new BasicDBObject("$gt", from));


        if (showAlias != null) {
            query.put("show.ref", new DBRef(db, "show", showAlias));
        }

        DBCursor episodes = db.getCollection("episode").find(query);

        List<EpisodeData> result = new ArrayList<>();
        for (DBObject e : episodes) {
            EpisodeData d = mapper.map(e, EpisodeData.class);
            d.setPersistent(true);

            if (d.getPlannedTo() == d.getRealTo()) {
                //todo
                Date nd = new Date();
                nd.setTime(d.getPlannedTo().getTime() + 30 * 60 * 1000);
                d.setRealTo(nd);
            }
            result.add(d);
        }

        return result;

    }




    public void setDb(DB db) {
        this.db = db;
    }
}
