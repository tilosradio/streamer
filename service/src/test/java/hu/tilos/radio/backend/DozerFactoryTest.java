package hu.tilos.radio.backend;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DozerFactoryTest {

    @Test
    public void detectMappings() throws Exception {
        //given
        DozerFactory factory = new DozerFactory();

        //when
        List<String> dozerFiles = factory.detectMappings();

        //then
        Assert.assertEquals(1, dozerFiles.size());
        Assert.assertTrue(dozerFiles.contains("comment.xml"));

    }
}