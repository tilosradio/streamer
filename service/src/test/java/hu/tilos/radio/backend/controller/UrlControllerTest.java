package hu.tilos.radio.backend.controller;

import hu.radio.tilos.model.Url;
import hu.tilos.radio.backend.TestUtil;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.input.UrlToSave;
import hu.tilos.radio.backend.data.response.CreateResponse;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class UrlControllerTest {

    @Inject
    UrlController controller;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData("UrlControllerTest.xml");
    }

    @Test
    public void create() throws Exception {
        //given
        UrlToSave url = new UrlToSave();
        url.setAddress("http://tiloshu");

        //when
        controller.getEntityManager().getTransaction().begin();
        CreateResponse createResponse = controller.create(1, url);
        controller.getEntityManager().getTransaction().commit();

        //then
        Url url1 = controller.getEntityManager().find(Url.class, createResponse.getId());
        assertThat(url1.getUrl(), equalTo("http://tiloshu"));
    }
}