package hu.tilos.radio.backend.controller;

import hu.tilos.radio.backend.DozerFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.MongoProducer;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, FongoCreator.class})
@ActivatedAlternatives(FongoCreator.class)
public class DataFixControllerTest {

    @Inject
    DataFixController controller;

    @Test
    public void fixTags() throws Exception {
        //given

        //when
        controller.fixTags();

        //then

    }

}