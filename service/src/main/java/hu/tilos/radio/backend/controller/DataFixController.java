package hu.tilos.radio.backend.controller;


import com.mongodb.*;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.tag.TagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Various functions to check data consistency.
 */
@Path("api/v1/fix")
public class DataFixController {


    private static final Logger LOG = LoggerFactory.getLogger(DataFixController.class);

    @Inject
    DB db;

    @Inject
    private TagUtil tagUtil;


    /**
     * @exclude
     */
    @GET
    @Path("/tags")
    @Security(role = Role.ADMIN)
    @Transactional
    public void fixTags() throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
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
