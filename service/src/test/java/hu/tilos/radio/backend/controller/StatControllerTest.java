package hu.tilos.radio.backend.controller;

import hu.tilos.radio.backend.ConfigurationProducer;
import hu.tilos.radio.backend.TestConfigProvider;
import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.controller.StatController;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.output.StatData;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class, ConfigurationProducer.class})
@ActivatedAlternatives(TestConfigProvider.class)
public class StatControllerTest {


    @Inject
    StatController controller;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }


    @Test
    public void testGetSummary() {
        //given

        //when
        StatData data = controller.getSummary();

        //then
        Assert.assertEquals(3, data.showCount);
        Assert.assertEquals(1, data.episodeCount);
    }
}