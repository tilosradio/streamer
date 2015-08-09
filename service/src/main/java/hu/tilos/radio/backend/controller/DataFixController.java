package hu.tilos.radio.backend.controller;


import com.mongodb.*;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.tag.TagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Various functions to check data consistency.
 */
public class DataFixController {


    private static final Logger LOG = LoggerFactory.getLogger(DataFixController.class);

    @Inject
    DB db;

    @Inject
    private TagUtil tagUtil;


    /**
     * @exclude
     */
    public void fixTags() {
        LOG.info("Starting to fix tags");
        DBCursor contents = db.getCollection("episode").find(new BasicDBObject("text", new BasicDBObject("$exists", true)));
        for (DBObject episode : contents) {
            BasicDBList tagObject = tagUtil.createTagObject(episode, "text", "content");
            if (tagObject.size() > 0) {
                LOG.info("Detected tags: " + tagObject);
            }
            episode.put("tags", tagObject);
            db.getCollection("episode").update(new BasicDBObject("_id", episode.get("_id")), episode);
        }
    }


}
