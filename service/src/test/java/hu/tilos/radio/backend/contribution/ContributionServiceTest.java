package hu.tilos.radio.backend.contribution;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.controller.internal.ContributionController;
import hu.tilos.radio.backend.data.input.ObjectReference;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.loadFrom;
import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


public class ContributionServiceTest {

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);


    @Inject
    ContributionController controller;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void save() throws Exception {
        //given
        String authorId = loadTo(fongoRule, "author", "author-author2.json");
        String showId = loadTo(fongoRule, "show", "show-vendeglo.json");
        ContributionToSave toSave = new ContributionToSave();
        toSave.setNick("joci");
        toSave.setAuthor(new ObjectReference(authorId));
        toSave.setShow(new ObjectReference(showId));

        //when
        controller.save(toSave);

        //then
        DBObject show = fongoRule.getDB().getCollection("show").findOne();
        JSONAssert.assertEquals(loadFrom("contributor-save-expected-show.json", showId, authorId), JSON.serialize(show), false);

        DBObject author = fongoRule.getDB().getCollection("author").findOne();
        JSONAssert.assertEquals(loadFrom("contributor-save-expected-author.json", authorId, showId), JSON.serialize(author), false);
    }


    @Test
    public void delete() throws Exception {
        //given
        String authorId = loadTo(fongoRule, "author", "author-author2.json");
        String showId = loadTo(fongoRule, "show", "show-vendeglo.json");
        ContributionToSave toSave = new ContributionToSave();
        toSave.setNick("joci");
        toSave.setAuthor(new ObjectReference(authorId));
        toSave.setShow(new ObjectReference(showId));
        controller.save(toSave);

        //when
        controller.delete(authorId, showId);

        //then
        DBObject show = fongoRule.getDB().getCollection("show").findOne();
        JSONAssert.assertEquals(loadFrom("contributor-delete-expected-show.json", showId, authorId), JSON.serialize(show), false);


        DBObject author = fongoRule.getDB().getCollection("author").findOne();
        System.out.println(JSON.serialize(author));
        JSONAssert.assertEquals(loadFrom("contributor-delete-expected-author.json", authorId, showId), JSON.serialize(author), false);
    }
}