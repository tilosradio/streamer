package hu.tilos.radio.backend.controller;


import hu.radio.tilos.model.*;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.converters.TagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;
import java.util.Set;

/**
 * Various functions to check data consistency.
 */
@Path("api/v1/fix")
public class DataFixController {

    @Inject
    private EntityManager entityManager;

    @Inject
    private TagUtil tagUtil;

    private static final Logger LOG = LoggerFactory.getLogger(DataFixController.class);

    /**
     *
     * @exclude
     */
    @GET
    @Path("/tags")
    @Security(role = Role.ADMIN)
    @Transactional
    public void fixTags() throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        LOG.info("Starting to fx tags");
        List<TextContent> contents = entityManager.createQuery("SELECT t FROM TextContent t WHERE t.content is not null").getResultList();
        for (TextContent text : contents) {

            if (text.getContent() != null) {

                LOG.info("Analyzing " + text.getId() + " / " + text.getAlias());
                Set<Tag> newTags = tagUtil.getTags(text.getContent());
                StringBuilder tags = new StringBuilder();
                for (Tag tag : newTags) {
                    tags.append(tag.getName() + ", ");
                }
                LOG.info("Detected tags: " + tags);
                tagUtil.updateTags(entityManager, text, newTags);
                entityManager.flush();

            }
        }
    }


    public void recalculateTags(TextContent text) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

    }


    public EntityManager getEntityManager() {
        return entityManager;
    }
}
