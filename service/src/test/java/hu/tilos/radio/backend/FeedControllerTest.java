package hu.tilos.radio.backend;

import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.episode.EpisodeUtil;
import hu.tilos.radio.backend.episode.PersistentEpisodeProvider;
import hu.tilos.radio.backend.episode.ScheduledEpisodeProvider;
import net.anzix.jaxrs.atom.Feed;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class, TestConfigProvider.class})
public class FeedControllerTest {

    @Inject
    FeedController feedController;

    @Before
    public void resetDatabase(){
        TestUtil.initTestData();
    }

    @Test
    public void testFeed() throws Exception {
        //given


        feedController.setServerUrl("http://tilos.hu");



        //when
        Feed feed = (Feed) feedController.feed("3utas", null).getEntity();

        //then
        JAXBContext jaxbc = JAXBContext.newInstance(Feed.class);
        Marshaller marshaller = jaxbc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(feed, System.out);

    }
}