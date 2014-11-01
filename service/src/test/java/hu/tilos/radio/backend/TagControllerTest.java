package hu.tilos.radio.backend;

import hu.radio.tilos.model.Mix;
import hu.radio.tilos.model.type.MixCategory;
import hu.radio.tilos.model.type.MixType;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.data.types.MixData;
import hu.tilos.radio.backend.data.types.MixSimple;
import hu.tilos.radio.backend.data.types.ShowSimple;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

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