package hu.tilos.radio.backend;

import junit.framework.Assert;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(CdiRunner.class)
@AdditionalClasses({TestConfigSource.class, ConfigurationProducer.class})
@ActivatedAlternatives(TestConfigSource.class)
public class ConfigurationProducerTest {

    @Inject
    @Configuration(name = "test.properties")
    private String config;

    @Test
    public void test() {
        Assert.assertEquals("asd", config);
    }
}