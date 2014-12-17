package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.data.input.EpisodeToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import hu.tilos.radio.backend.data.types.EpisodeData;
import hu.tilos.radio.backend.data.types.ShowSimple;
import hu.tilos.radio.backend.data.types.TextData;
import org.dozer.DozerBeanMapper;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import javax.inject.Inject;
import java.text.SimpleDateFormat;

import static hu.tilos.radio.backend.MongoTestUtil.loadFrom;
import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, ConfigurationProducer.class})
@ActivatedAlternatives({FongoCreator.class, TestConfigProvider.class})
public class EpisodeControllerTest {

    @Inject
    EpisodeController controller;

    @Inject
    DozerBeanMapper mapper;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void testGet() throws Exception {
        //given
        loadTo(fongoRule, "episode", "episode-episode2.json");

        //when
        EpisodeData episode = controller.get("2");

        //then
        Assert.assertNotNull(episode.getText());
        Assert.assertEquals("Jo musor", episode.getText().getTitle());
        Assert.assertEquals("http://tilos.hu/mp3/tilos-20140411-100000-120000.m3u", episode.getM3uUrl());
    }


    @Test
    public void testGetByDate() throws Exception {
        //given
        String showId = loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "episode", "episode-episode2.json", showId);

        //when
        EpisodeData episode = controller.getByDate("3utas", 2014, 04, 11);

        //then
        Assert.assertNotNull(episode.getText());
        Assert.assertEquals("Jo musor", episode.getText().getTitle());
    }

    @Test
    public void testCreateEpisode() throws Exception {
        //given
        String showId = loadTo(fongoRule, "show", "show-3utas.json");

        EpisodeToSave episode = new EpisodeToSave();
        episode.setPlannedFrom(TestUtil.YYYYMMDDHHMM.parse("201405011200"));
        episode.setPlannedTo(TestUtil.YYYYMMDDHHMM.parse("201405011300"));

        ShowSimple simple = new ShowSimple();
        simple.setId(showId);
        episode.setShow(simple);

        TextData td = new TextData();
        td.setTitle("Title");
        td.setContent("ahoj #teg ahoj");
        episode.setText(td);

        //when
        CreateResponse createResponse = controller.create(episode);

        //then
        DBObject mongoEpisode = fongoRule.getDB().getCollection("episode").findOne();
        System.out.println(JSON.serialize(mongoEpisode));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String expectedUser = loadFrom("episode-create-expected.json", createResponse.getId(), showId, sdf.format(mongoEpisode.get("created")));
        //TODO:

    }

    @Test
    public void testUpdateEpisode() throws Exception {
        //given
        String showId = loadTo(fongoRule, "show", "show-3utas.json");
        String episodeId = loadTo(fongoRule, "episode", "episode-episode1.json", showId);


        EpisodeToSave episode = mapper.map(controller.get(episodeId), EpisodeToSave.class);
        episode.setText(new TextData());
        episode.setPlannedFrom(TestUtil.YYYYMMDDHHMM.parse("201405011200"));
        episode.setPlannedTo(TestUtil.YYYYMMDDHHMM.parse("201405011300"));

        episode.getText().setContent("ez jobb #kukac de a harom nincs @szemely is van");

        //when
        UpdateResponse createResponse = controller.update(episodeId, episode);

        //then
        DBObject mEpisode = fongoRule.getDB().getCollection("episode").findOne();
        DBObject text = (DBObject) mEpisode.get("text");
        Assert.assertNotNull(text);
        Assert.assertEquals("ez jobb #kukac de a harom nincs @szemely is van", text.get("content"));


    }
}