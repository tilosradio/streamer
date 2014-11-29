package hu.tilos.radio.backend.episode;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.MongoProducer;
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
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class})
@ActivatedAlternatives(FongoCreator.class)
public class PersistentEpisodeProviderTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    PersistentEpisodeProvider p;

    @Inject
    private FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }


    @Test
    public void testListEpisode() throws Exception {
        //given
        String showId = loadTo(fongoRule, "show", "show-3utas.json");
        loadTo(fongoRule, "episode", "episode-episode1.json", showId);
        loadTo(fongoRule, "episode", "episode-episode2.json", showId);


        //when
        List<EpisodeData> episodes = p.listEpisode(showId, SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(2, episodes.size());
        Assert.assertNotNull(episodes.get(1).getShow());
        Assert.assertNotNull(episodes.get(1).getText());
        Assert.assertEquals("Jo musor", episodes.get(1).getText().getTitle());


    }
}