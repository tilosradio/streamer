package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.MongoProducer;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, FongoCreator.class})
@ActivatedAlternatives(FongoCreator.class)
public class DataFixControllerTest {

    @Inject
    DataFixController controller;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void fixTags() throws Exception {
        //given
        loadTo(fongoRule, "episode", "episode-episode3.json");

        //when
        controller.fixTags();

        //then
        DBObject episode = fongoRule.getDB().getCollection("episode").findOne();
        Assert.assertNotNull(episode.get("tags"));

    }

}