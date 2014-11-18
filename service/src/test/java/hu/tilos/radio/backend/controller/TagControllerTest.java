package hu.tilos.radio.backend.controller;

import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.controller.TagController;
import hu.tilos.radio.backend.converters.MappingFactory;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class TagControllerTest {

    @Inject
    TagController controller;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    public void testGet() {

        //given

        //when
        controller.get(1);

        //then

    }


}