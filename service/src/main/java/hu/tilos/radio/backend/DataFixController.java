package hu.tilos.radio.backend;


import hu.radio.tilos.model.*;
import hu.tilos.radio.backend.converters.TagUtil;
import hu.tilos.radio.backend.data.SearchResponse;
import hu.tilos.radio.backend.data.SearchResponseElement;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.apache.lucene.util.Version.LUCENE_48;

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
