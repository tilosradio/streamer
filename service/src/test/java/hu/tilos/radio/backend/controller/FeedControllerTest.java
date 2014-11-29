package hu.tilos.radio.backend.controller;

import com.github.fakemongo.junit.FongoRule;
import hu.tilos.radio.backend.*;
import net.anzix.jaxrs.atom.Feed;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, FongoCreator.class, ConfigurationProducer.class})
@ActivatedAlternatives({FongoCreator.class, TestConfigProvider.class})
public class FeedControllerTest {

    @Inject
    FeedController feedController;

    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }

    @Test
    public void testFeed() throws Exception {
        //given
        loadTo(fongoRule, "show", "show-3utas.json");

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