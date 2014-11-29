package hu.tilos.radio.backend.controller;


import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import hu.radio.tilos.model.Role;
import hu.tilos.radio.backend.Security;
import hu.tilos.radio.backend.data.output.SearchResponse;
import hu.tilos.radio.backend.data.output.SearchResponseElement;
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

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.lucene.util.Version.LUCENE_48;

@Path("api/v1/search")
public class SearchController {

    private static final Logger LOG = LoggerFactory.getLogger(SearchController.class);

    @Inject
    private DB db;

    private Directory index;

    private Directory getIndex() throws IOException {
        if (index == null) {
            index = new RAMDirectory();
            index();
        }
        return index;
    }

    private void createIndex() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(LUCENE_48, createAnalyzer());

        IndexWriter w = new IndexWriter(getIndex(), config);
        addAuthors(w);
        addShows(w);
        addPages(w);
        addEpisodes(w);
        w.close();

    }

    public String index() {
        try {
            createIndex();
            return "Indexing is finished";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "ERROR";
        }
    }

    public Analyzer createAnalyzer() {
        return new StopwordAnalyzerBase(LUCENE_48) {

            @Override
            protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
                final StandardTokenizer src = new StandardTokenizer(matchVersion, reader);
                src.setMaxTokenLength(10);

                TokenStream tok = new StandardFilter(matchVersion, src);
                tok = new LowerCaseFilter(matchVersion, tok);
                tok = new ASCIIFoldingFilter(tok, true);
                tok = new StopFilter(matchVersion, tok, stopwords);

                return new TokenStreamComponents(src, tok) {
                    @Override
                    protected void setReader(final Reader reader) throws IOException {
                        src.setMaxTokenLength(10);
                        super.setReader(reader);
                    }
                };
            }
        };

    }


    /**
     * @param search
     * @return
     * @throws IOException
     * @throws ParseException
     * @exclude
     */
    @Path(value = "query")
    @GET
    @Produces("application/json")
    @Security(role = Role.GUEST)
    public SearchResponse search(@QueryParam("q") String search) throws IOException, ParseException {

        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                LUCENE_48,
                new String[]{"name", "alias", "definition", "introduction", "description"},
                createAnalyzer());
        queryParser.setAllowLeadingWildcard(true);

        org.apache.lucene.search.Query q = queryParser.parse(search);

        IndexReader reader = IndexReader.open(getIndex());
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        SearchResponse r = new SearchResponse();
        for (int i = 0; i < hits.length; ++i) {
            SearchResponseElement e = new SearchResponseElement();
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            e.setScore(hits[i].score);
            e.setAlias(safe(d.getField("alias")));
            e.setType(safe(d.getField("type")));
            e.setUri(safe(d.getField("uri")));
            e.setTitle(safe(d.getField("name")));
            e.setDescription(safe(d.getField("description")));
            r.addElement(e);
        }
        return r;
    }

    private String safe(IndexableField type) {
        if (type == null) {
            return null;
        }
        return type.stringValue();
    }

    private void addAuthors(IndexWriter w) throws IOException {
        DBCursor authors = db.getCollection("author").find();
        for (DBObject a : authors) {
            Document doc = new Document();
            doc.add(new TextField("name", safe(a, "name"), Field.Store.YES));

            doc.add(new TextField("alias", safe(a, "alias"), Field.Store.NO));

            if (a.get("introduction") != null) {
                doc.add(new TextField("introduction", a.get("introduction").toString(), Field.Store.NO));
                doc.add(new TextField("description", shorten(a.get("introduction").toString(), 100), Field.Store.YES));
            } else {
                doc.add(new TextField("description", "", Field.Store.YES));
            }

            doc.add(new TextField("type", "author", Field.Store.YES));
            doc.add(new TextField("uri", "/author/" + safe(a, "alias"), Field.Store.YES));
            w.addDocument(doc);
        }

    }

    private String shorten(String text, int i) {
        if (text.length() > 100) {
            return text.substring(0, 99) + "...";
        } else {
            return text;
        }
    }

    private void addShows(IndexWriter w) throws IOException {
        DBCursor shows = db.getCollection("show").find();
        for (DBObject show : shows) {
            Document doc = new Document();
            doc.add(new TextField("name", safe(show, "name"), Field.Store.YES));
            doc.add(new TextField("alias", safe(show, "alias"), Field.Store.NO));
            doc.add(new TextField("description", safe(show, "description"), Field.Store.YES));
            doc.add(new TextField("introduction", safe(show, "description"), Field.Store.NO));
            doc.add(new TextField("type", "show", Field.Store.YES));
            doc.add(new TextField("uri", "/show/" + safe(show, "alias"), Field.Store.YES));

            w.addDocument(doc);
        }

    }

    private void addPages(IndexWriter w) throws IOException {
        DBCursor pages = db.getCollection("page").find();
        for (DBObject page : pages) {
            Document doc = new Document();

            doc.add(new TextField("content", safe(page, "content"), Field.Store.NO));
            doc.add(new TextField("alias", safe(page, "alias"), Field.Store.YES));
            doc.add(new TextField("name", safe(page, "title"), Field.Store.YES));
            doc.add(new TextField("description", shorten(safe(page, "content"), 100), Field.Store.YES));
            doc.add(new TextField("content", safe(page, "content"), Field.Store.NO));
            doc.add(new TextField("type", "page", Field.Store.YES));
            if (safe(page, "alias").length() > 0) {
                doc.add(new TextField("uri", "/page/" + page.get("alias"), Field.Store.YES));
            } else {
                doc.add(new TextField("uri", "/page/" + page.get("_id"), Field.Store.YES));
            }
            w.addDocument(doc);
        }

    }

    private void addEpisodes(final IndexWriter w) throws IOException {

        DBCursor episodes = db.getCollection("episode").find();
        for (DBObject e : episodes) {
            DBObject text = (DBObject) e.get("text");
            if (text != null) {


                Document doc = new Document();
                doc.add(new TextField("content", safe(text, "content"), Field.Store.NO));
                //doc.add(new TextField("alias", safe(page.getAlias()), Field.Store.NO));
                doc.add(new TextField("name", safe(text, "title"), Field.Store.YES));
                doc.add(new TextField("description", shorten(safe(text, "content"), 100), Field.Store.YES));
                doc.add(new TextField("content", safe(text, "content"), Field.Store.NO));
                doc.add(new TextField("type", "episode", Field.Store.YES));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date plannedFrom = (Date) e.get("plannedFrom");
                String alias = (String) ((DBObject) e.get("show")).get("alias");
                doc.add(new TextField("uri", "/episode/" + alias + "/" + dateFormat.format(new Date(plannedFrom.getTime())), Field.Store.YES));
                try {
                    w.addDocument(doc);
                } catch (IOException ex) {
                    LOG.error("Can't fixTags episode record", ex);
                }
            }
        }
    }


    private String safe(DBObject object, String content) {
        if (object.get(content) == null) {
            return "";
        }
        return object.get(content).toString();
    }


}
