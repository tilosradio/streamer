package hu.tilos.radio.backend.episode;

import ch.qos.logback.classic.Level;
import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.types.EpisodeData;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class PersistentEpisodeProviderTest {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    PersistentEpisodeProvider p;

    @BeforeClass
    public static void setUp() {
        TestUtil.initTestData();
    }

    @Test
    public void testListEpisode() throws Exception {
        //given


        //when
        List<EpisodeData> episodes = p.listEpisode(1, SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(2, episodes.size());
        Assert.assertNotNull(episodes.get(0).getShow());
        Assert.assertNotNull(episodes.get(0).getText());
        Assert.assertEquals("Jo kis beszelgetes 1", episodes.get(0).getText().getTitle());
        Assert.assertNotNull(episodes.get(1).getText());
        Assert.assertEquals("Jo musor", episodes.get(1).getText().getTitle());


    }
}