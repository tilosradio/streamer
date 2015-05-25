package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.GuiceRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


public class DataFixControllerTest {

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);
    
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