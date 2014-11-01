package hu.tilos.radio.backend;

import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.StatData;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import static org.junit.Assert.*;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
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