package hu.tilos.radio.backend.controller;

import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.controller.DataFixController;
import hu.tilos.radio.backend.converters.MappingFactory;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class DataFixControllerTest {

    @Inject
    DataFixController controller;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    public void fixTags() throws Exception {
        //given

        //when
        controller.getEntityManager().getTransaction().begin();
        controller.fixTags();
        controller.getEntityManager().getTransaction().rollback();

        //then

    }

}