package hu.tilos.radio.backend.controller;

import hu.tilos.radio.backend.ConfigurationProducer;
import hu.tilos.radio.backend.TestConfigProvider;
import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.controller.FeedController;
import hu.tilos.radio.backend.converters.MappingFactory;
import net.anzix.jaxrs.atom.Feed;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class, TestConfigProvider.class, ConfigurationProducer.class})
@ActivatedAlternatives(TestConfigProvider.class)
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