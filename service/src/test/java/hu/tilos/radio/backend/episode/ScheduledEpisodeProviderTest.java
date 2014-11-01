package hu.tilos.radio.backend.episode;

import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.types.EpisodeData;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class ScheduledEpisodeProviderTest {

    private static DataSource ds;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Inject
    ScheduledEpisodeProvider p;

    @BeforeClass
    public static void setUp() {
        ds = TestUtil.initDatasource();
        TestUtil.initTestData();
    }

    @Test
    public void testListEpisode() throws Exception {
        //given

        //when
        List<EpisodeData> episodes = p.listEpisode(1, SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(3, episodes.size());

    }

    @Test
    public void testListEpisodeWithBase() throws Exception {
        //given

        //when
        List<EpisodeData> episodes = p.listEpisode(3, SDF.parse("2014-04-03 12:00:00"), SDF.parse("2014-05-03 12:00:00"));

        //then
        Assert.assertEquals(2, episodes.size());
    }
}