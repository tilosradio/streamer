package hu.tilos.radio.backend.episode;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.data.types.EpisodeData;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, ConfigurationProducer.class})
@ActivatedAlternatives({FongoCreator.class, TestConfigProvider.class})
public class ScheduledEpisodeProviderTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    ScheduledEpisodeProvider p;

    private FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule = new FongoRule();
    }

    @Test
    public void testListEpisode() throws Exception {
        //given
        p.setDb(fongoRule.getDB());
        loadTo(fongoRule,"show","show-3utas.json");

        //when
        List<EpisodeData> episodes = p.listEpisode("1", SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(3, episodes.size());

    }

    @Test
    public void testListEpisodeWithBase() throws Exception {
        //given
        p.setDb(fongoRule.getDB());
        loadTo(fongoRule,"show","show-vendeglo.json");

        //when
        List<EpisodeData> episodes = p.listEpisode("3", SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(2, episodes.size());
    }
}