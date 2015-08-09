package hu.tilos.radio.backend.contribution;

import com.mongodb.*;
import hu.tilos.radio.backend.data.response.OkResponse;
import org.bson.types.ObjectId;

import javax.inject.Inject;

public class ContributionService {

    @Inject
    DB db;

    public OkResponse delete(String authorId, String showId) {
        BasicDBObject authorQuery = new BasicDBObject("_id", new ObjectId(authorId));
        DBObject author = db.getCollection("author").findOne(authorQuery);

        BasicDBObject showQuery = new BasicDBObject("_id", new ObjectId(showId));
        DBObject show = db.getCollection("show").findOne(showQuery);

        deleteFromCollection((BasicDBList) author.get("contributions"), "show", show.get("_id"));
        db.getCollection("author").update(authorQuery, author);
        deleteFromCollection((BasicDBList) show.get("contributors"), "author", author.get("_id"));
        db.getCollection("show").update(showQuery, show);
        return new OkResponse("deleted");
    }

    private void deleteFromCollection(BasicDBList collection, String key, Object id) {
        if (collection != null) {
            for (int i = 0; i < collection.size(); i++) {
                DBObject element = (DBObject) collection.get(i);
                DBObject subElement = (DBObject) element.get(key);
                DBRef ref = (DBRef) subElement.get("ref");
                if (id.equals(ref.getId())) {
                    collection.remove(i);
                    return;
                }
            }

        }

    }

    public String save(ContributionToSave contributionToSave) {
        BasicDBObject authorQuery = new BasicDBObject("_id", new ObjectId(contributionToSave.getAuthor().getId()));
        DBObject author = db.getCollection("author").findOne(authorQuery);

        BasicDBObject showQuery = new BasicDBObject("_id", new ObjectId(contributionToSave.getShow().getId()));
        DBObject show = db.getCollection("show").findOne(showQuery);

        addContributionToAuthor(contributionToSave, authorQuery, author, show);
        addContributorToShow(contributionToSave, showQuery, author, show);
        return "ok";

    }

    private void addContributorToShow(ContributionToSave contributionToSave, BasicDBObject showQuery, DBObject author, DBObject show) {
        BasicDBList contributiors = (BasicDBList) show.get("contributors");
        if (contributiors == null) {
            contributiors = new BasicDBList();
            show.put("contributors", contributiors);
        }

        BasicDBObject contributor = new BasicDBObject();
        contributor.put("nick", contributionToSave.getNick());
        BasicDBObject contributorAuthor = new BasicDBObject();
        contributorAuthor.put("alias", author.get("alias"));
        contributorAuthor.put("ref", new DBRef(db, "author", author.get("_id")));
        contributor.put("author", contributorAuthor);
        contributiors.add(contributor);
        db.getCollection("show").update(showQuery, show);
    }

    private void addContributionToAuthor(ContributionToSave contributionToSave, BasicDBObject authorQuery, DBObject author, DBObject show) {
        BasicDBList contributions = (BasicDBList) author.get("contributions");
        if (contributions == null) {
            contributions = new BasicDBList();
            author.put("contributions", contributions);
        }

        BasicDBObject contribution = new BasicDBObject();
        contribution.put("nick", contributionToSave.getNick());
        BasicDBObject contributionShow = new BasicDBObject();
        contributionShow.put("alias", show.get("alias"));
        contributionShow.put("name", show.get("name"));
        contributionShow.put("ref", new DBRef(db, "show", show.get("_id")));
        contribution.put("show", contributionShow);
        contributions.add(contribution);
        db.getCollection("author").update(authorQuery, author);
    }

}
