package hu.tilos.radio.backend.text;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DBObject;
import hu.tilos.radio.backend.*;
import hu.tilos.radio.backend.data.response.CreateResponse;
import hu.tilos.radio.backend.data.response.UpdateResponse;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

import static hu.tilos.radio.backend.MongoTestUtil.loadTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(CdiRunner.class)
@AdditionalClasses({MongoProducer.class, DozerFactory.class, ConfigurationProducer.class})
@ActivatedAlternatives({FongoCreator.class, TestConfigProvider.class})
public class TextControllerTest {

    @Inject
    TextService controller;


    @Inject
    FongoRule fongoRule;

    @Rule
    public FongoRule fongoRule() {
        return fongoRule;
    }


    @Test
    public void get() throws Exception {
        //given

        loadTo(fongoRule, "page", "page-page1.json");

        //when
        TextData page = controller.get("1", "page");

        //then
        assertThat(page.getTitle(), equalTo("tamogatas"));
        assertThat(page.getContent(), equalTo("<p>asd</p>"));

    }

    @Test
    public void getMd() throws Exception {
        //given

        loadTo(fongoRule, "page", "page-page3.json");

        //when
        TextData page = controller.get("1", "page");

        //then
        assertThat(page.getTitle(), equalTo("tamogatas"));
        assertThat(page.getFormatted(), equalTo("<p>asd</p><p>asd</p><p><a href=\"http://pipacs.com\">test</a></p>"));

    }

    @Test
    public void list() throws Exception {
        //given
        loadTo(fongoRule, "page", "page-page1.json");

        //when
        List<TextDataSimple> pages = controller.list("page", 1);

        //then
        assertThat(pages.size(), equalTo(1));

    }

    @Test
    public void update() throws Exception {
        //given
        loadTo(fongoRule, "page", "page-page1.json");
        TextToSave textToSave = new TextToSave();
        textToSave.setTitle("ahoj");
        textToSave.setContent("ahoj2");

        //when
        UpdateResponse page = controller.update("page", "1", textToSave);

        //then
        DBObject textContent = fongoRule.getDB().getCollection("page").findOne();
        assertThat((String) textContent.get("title"), equalTo("ahoj"));

    }

    @Test
    public void create() throws Exception {
        //given

        TextToSave textToSave = new TextToSave();
        textToSave.setTitle("ahoj");
        textToSave.setContent("ahoj2");


        //when

        CreateResponse response = controller.create("page", textToSave);


        //then
        DBObject textContent = fongoRule.getDB().getCollection("page").findOne();
        assertThat((String) textContent.get("title"), equalTo("ahoj"));

    }
}