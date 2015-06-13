package hu.tilos.radio.backend.episode.util;


import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.episode.EpisodeData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

public class EpisodeUtilTest {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Rule
    public GuiceRunner guice = new GuiceRunner(this);

    @Inject
    EpisodeUtil episodeUtil;

    @Inject
    private FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }


    @Test
    public void testListEpisode() throws Exception {
        //given
        String show3utas = loadTo(fongoRule, "show", "show-3utas.json");
        String showVendeglo = loadTo(fongoRule, "show", "show-vendeglo.json");
        loadTo(fongoRule, "episode", "episode-episode1.json", show3utas);
        loadTo(fongoRule, "episode", "episode-episode2.json", show3utas);
        loadTo(fongoRule, "episode", "episode-vendeglo-extra.json", showVendeglo);

        //when
        List<EpisodeData> episodes = episodeUtil.getEpisodeData(show3utas, SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        episodes.stream().forEach(e -> System.out.println(e));
        Assert.assertEquals(5, episodes.size());
        Assert.assertNotNull(episodes.get(1).getShow());
        Assert.assertNotNull(episodes.get(1).getText());
        Assert.assertEquals("Jo musor", episodes.get(1).getText().getTitle());
        Assert.assertEquals(6, fongoRule.getDB().getCollection("episode").find().count());


    }
}