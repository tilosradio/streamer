package hu.tilos.radio.backend;

import hu.radio.tilos.model.TextContent;
import hu.tilos.radio.backend.converters.MappingFactory;
import hu.tilos.radio.backend.data.CreateResponse;
import hu.tilos.radio.backend.data.UpdateResponse;
import hu.tilos.radio.backend.data.input.TextToSave;
import hu.tilos.radio.backend.data.types.TextData;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CdiRunner.class)
@AdditionalClasses({MappingFactory.class, TestUtil.class})
public class TextControllerTest {

    @Inject
    TextController controller;

    @Before
    public void resetDatabase() {
        TestUtil.initTestData();
    }

    @Test
    public void get() throws Exception {
        //given


        //when
        TextData page = controller.get("1", "page");

        //then
        assertThat(page.getTitle(), equalTo("tamogatas"));

    }

    @Test
    public void list() throws Exception {
        //given

        //when
        List<TextData> pages = controller.list("page");

        //then
        assertThat(pages.size(), equalTo(1));

    }

    @Test
    public void update() throws Exception {
        //given
        TextToSave textToSave = new TextToSave();
        textToSave.setTitle("ahoj");
        textToSave.setContent("ahoj2");
        EntityManager em = controller.getEntityManager();

        //when
        em.getTransaction().begin();
        UpdateResponse page = controller.update("page", "1", textToSave);
        em.getTransaction().commit();

        //then
        TextContent textContent = em.find(TextContent.class, 1);
        assertThat(textContent.getTitle(),equalTo("ahoj"));

    }

    @Test
    public void create() throws Exception {
        //given
        TextToSave textToSave = new TextToSave();
        textToSave.setTitle("ahoj");
        textToSave.setContent("ahoj2");
        EntityManager em = controller.getEntityManager();

        //when
        em.getTransaction().begin();
        CreateResponse response = controller.create("page", textToSave);
        em.getTransaction().commit();

        //then
        TextContent textContent = em.find(TextContent.class, response.getId());
        assertThat(textContent.getTitle(),equalTo("ahoj"));

    }
}